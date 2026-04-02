package dalbit.adapter.rest.internal.auth.in;

import dalbit.adapter.rest.internal.auth.dto.request.QueueResourceAuthRequest;
import dalbit.adapter.rest.internal.auth.dto.request.QueueTopicAuthRequest;
import dalbit.adapter.rest.internal.auth.dto.request.QueueUserAuthRequest;
import dalbit.adapter.rest.internal.auth.dto.request.QueueVhostAuthRequest;
import dalbit.application.rest.internal.auth.useCase.AuthenticateQueueUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/queue/auth")
public class QueueAuthController {

    private final AuthenticateQueueUseCase authenticateQueueUseCase;

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authenticateUser(@ModelAttribute QueueUserAuthRequest request) {
        try {
            boolean isAuthenticated = authenticateQueueUseCase.authenticateConnection(
                request.username(),
                request.password()
            );

            if (isAuthenticated) {
                log.info("[RabbitMQ Auth] 연결 승인 - username: {}", request.username());
                return "allow";
            } else {
                log.warn("[RabbitMQ Auth] 연결 거부 (인증 실패) - username: {}", request.username());
                return "deny";
            }
        } catch (Exception e) {
            log.error("[RabbitMQ Auth] 연결 인증 중 시스템 에러 발생 - username: {}", request.username(), e);
            return "deny";
        }
    }

    @PostMapping(value = "/vhost", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authorizeVhost(@ModelAttribute QueueVhostAuthRequest request) {
        return "allow";
    }

    @PostMapping(value = "/resource", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authorizeResource(@ModelAttribute QueueResourceAuthRequest request) {
        try {
            boolean isAuthorized = authenticateQueueUseCase.isResourceAuthorized(
                request.username(),
                request.resource(),
                request.name(),
                request.permission()
            );

            return isAuthorized ? "allow" : "deny";
        } catch (Exception e) {
            log.error("[RabbitMQ Auth] 리소스 인가 중 시스템 에러 발생 - username: {}", request.username(), e);
            return "deny";
        }
    }

    @PostMapping(value = "/topic", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authorizeTopic(@ModelAttribute QueueTopicAuthRequest request) {
        try {
            boolean isAuthorized = authenticateQueueUseCase.isTopicAuthorized(
                request.username(),
                request.routing_key(),
                request.permission()
            );

            return isAuthorized ? "allow" : "deny";
        } catch (Exception e) {
            log.error("[RabbitMQ Auth] 토픽 인가 중 시스템 에러 발생 - username: {}", request.username(), e);
            return "deny";
        }
    }
}
