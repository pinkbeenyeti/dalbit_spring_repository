package dalbit.application.persistence.redis.auth.port;

import java.util.Optional;

public interface ManageRefreshTokenPort {
    Optional<String> getRefreshToken(String userExternalId);
    void saveRefreshToken(String userExternalId, String refreshToken);
    void deleteRefreshToken(String userExternalId);
}
