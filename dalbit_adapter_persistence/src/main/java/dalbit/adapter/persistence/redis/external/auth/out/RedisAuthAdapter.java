package dalbit.adapter.persistence.redis.external.auth.out;

import dalbit.application.persistence.redis.auth.port.ManageRefreshTokenPort;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisAuthAdapter implements ManageRefreshTokenPort {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationTime;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<String> getRefreshToken(String userExternalId) {
        String key = generateKey(userExternalId);
        String token = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    @Override
    public void saveRefreshToken(String userExternalId, String refreshToken) {
        String key = generateKey(userExternalId);

        redisTemplate.opsForValue().set(
            key,
            refreshToken,
            Duration.ofMillis(refreshTokenExpirationTime)
        );

        log.debug("[Redis] Refresh Token 저장 완료 (Sliding Window 갱신) - userId: {}", userExternalId);
    }

    @Override
    public void deleteRefreshToken(String userExternalId) {
        String key = generateKey(userExternalId);

        Boolean deleted = redisTemplate.delete(key);

        if (deleted) {
            log.info("[Redis] Refresh Token 강제 삭제 (로그아웃/만료 처리) - userId: {}", userExternalId);
        }
    }

    private String generateKey(String userExternalId) {
        String KEY_PREFIX = "RT:USER:";
        return KEY_PREFIX + userExternalId;
    }
}


