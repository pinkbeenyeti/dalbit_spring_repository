package dalbit.adapter.persistence.jpa.external.user.out;

import dalbit.adapter.persistence.jpa.external.user.entity.UserJpaEntity;
import dalbit.adapter.persistence.jpa.external.user.mapper.UserJpaMapper;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.application.persistence.jpa.user.port.SaveUserPort;
import dalbit.domain.user.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserJpaMapper userJpaMapper;

    @Override
    public User saveUser(User user) {
        UserJpaEntity entity = userJpaRepository.save(userJpaMapper.toEntity(user));
        return userJpaMapper.toDomain(entity);
    }

    @Override
    @Cacheable(value = "userExternalIdToId", key = "#externalId")
    public Optional<Long> loadUserIdByExternalId(String externalId) {
        return userJpaRepository.findIdByExternalId(externalId);
    }

    @Override
    public Optional<User> loadUserByUserId(Long userId) {
        return userJpaRepository.findById(userId)
            .map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> loadUserByExternalId(String externalId) {
        return userJpaRepository.findByExternalId(externalId)
            .map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> loadUserByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId)
            .map(userJpaMapper::toDomain);
    }
}
