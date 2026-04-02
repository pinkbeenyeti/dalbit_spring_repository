package dalbit.application.rest.internal.auth.service;

import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.application.persistence.jpa.device.port.LoadDevicePort;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.application.rest.internal.auth.useCase.AuthenticateQueueUseCase;
import dalbit.domain.device.Device;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueAuthService implements AuthenticateQueueUseCase {

    private final VerifyTokenPort verifyTokenPort;
    private final LoadDevicePort loadDevicePort;
    private final LoadUserPort loadUserPort;

    private static final String DEVICE_PREFIX = "dev_";
    private static final String USER_PREFIX = "usr_";
    private static final Pattern TOPIC_SERIAL_PATTERN = Pattern.compile(".*SN-?(\\w+)$");

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateConnection(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        if (username.startsWith(USER_PREFIX)) {
            if (!verifyTokenPort.validateToken(password)) {
                log.warn("[RabbitMQ Auth] 유효하지 않거나 만료된 JWT 토큰 접속 시도 - username: {}", username);
                return false;
            }

            String userExternalId = verifyTokenPort.getExternalIdFromToken(password);
            String requestedUserExternalId = username.substring(USER_PREFIX.length());

            return requestedUserExternalId.equals(userExternalId);
        }

        if (username.startsWith(DEVICE_PREFIX)) {
            String requestedSerialNumber = username.substring(DEVICE_PREFIX.length());

            Device device = loadDevicePort.loadDeviceBySerialNumber(requestedSerialNumber)
                .orElse(null);

            if (device == null) {
                log.warn("[RabbitMQ Auth] 등록되지 않은 디바이스 접속 시도: {}", requestedSerialNumber);
                return false;
            }

            return device.verifyDeviceSecret(password);
        }

        log.warn("[RabbitMQ Auth] 허용되지 않은 Prefix 형식의 접속 시도: {}", username);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResourceAuthorized(String username, String resourceType, String name, String permission) {
        if ("exchange".equals(resourceType)) {
            if ("configure".equals(permission)) {
                return false;
            }

            return "amq.topic".equals(name) || "amq.direct".equals(name);
        }

        if ("queue".equals(resourceType)) {
            String identifier = username.substring(USER_PREFIX.length());
            boolean isOwnQueue = name.contains(identifier);
            
            if (!isOwnQueue) {
                log.warn("[RabbitMQ Auth] 타인 소유 가능성 있는 큐 접근 차단 - username: {}, queue: {}", username, name);
            }

            return isOwnQueue;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "queueAuth", key = "#username + ':' + #routingKey")
    public boolean isTopicAuthorized(String username, String routingKey, String permission) {
        if (username == null || routingKey == null) {
            return false;
        }

        if (username.startsWith(USER_PREFIX)) {
            Matcher matcher = TOPIC_SERIAL_PATTERN.matcher(routingKey);
            if (!matcher.find()) {
                log.warn("[RabbitMQ Auth] 토픽 패턴 불일치 (시리얼 번호 미포함) - username: {}, topic: {}", username, routingKey);
                return false;
            }

            String serialNumber = matcher.group(1);
            String userExternalId = username.substring(USER_PREFIX.length());

            Optional<Long> userId = loadUserPort.loadUserIdByExternalId(userExternalId);
            Optional<Device> device = loadDevicePort.loadDeviceBySerialNumber(serialNumber);

            if (userId.isEmpty() || device.isEmpty()) {
                log.warn("[RabbitMQ Auth] 유저 또는 디바이스 정보 없음 - username: {}, serial: {}", username, serialNumber);
                return false;
            }

            boolean isOwner = userId.get().equals(device.get().getUserId());
            if (!isOwner) {
                log.warn("[RabbitMQ Auth] 소유권 없는 디바이스 토픽 접근 시도 - username: {}, topic: {}", username, routingKey);
            }

            return isOwner;
        }

        if (username.startsWith(DEVICE_PREFIX)) {
            String serialNumber = username.substring(DEVICE_PREFIX.length());
            boolean isAllowed = routingKey.contains(serialNumber);
            
            if (!isAllowed) {
                log.warn("[RabbitMQ Auth] 타 디바이스 토픽 접근 시도 차단 - username: {}, topic: {}", username, routingKey);
            }

            return isAllowed;
        }

        return false;
    }
}
