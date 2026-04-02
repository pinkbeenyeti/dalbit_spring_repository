package dalbit.application.persistence.jpa.user.port;

import dalbit.domain.user.User;
import java.util.Optional;

public interface LoadUserPort {
    Optional<Long> loadUserIdByExternalId(String externalId);
    Optional<User> loadUserByUserId(Long userId);
    Optional<User> loadUserByExternalId(String externalId);
    Optional<User> loadUserByProviderId(String providerId);
}
