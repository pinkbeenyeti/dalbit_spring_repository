package dalbit.adapter.persistence.jpa.external.user.mapper;

import dalbit.adapter.persistence.jpa.external.user.entity.UserJpaEntity;
import dalbit.domain.user.User;
import dalbit.domain.user.UserEmail;
import dalbit.domain.user.UserName;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        return UserJpaEntity.builder()
            .id(user.getId())
            .externalId(user.getExternalId())
            .providerId(user.getProviderId())
            .name(user.getName().getValue())
            .email(user.getEmail().getValue())
            .role(user.getRole())
            .build();
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;

        return User.builder()
            .id(entity.getId())
            .externalId(entity.getExternalId())
            .providerId(entity.getProviderId())
            .name(UserName.of(entity.getName()))
            .email(UserEmail.of(entity.getEmail()))
            .role(entity.getRole())
            .build();
    }
}
