package dalbit.adapter.persistence.jpa.external.userDevice.mapper;

import dalbit.adapter.persistence.jpa.external.userDevice.entity.UserDeviceJpaEntity;
import dalbit.domain.userDevice.UserDevice;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceJpaMapper {

    public UserDeviceJpaEntity toEntity(UserDevice userDevice) {
        if (userDevice == null) return null;

        return UserDeviceJpaEntity.builder()
            .id(userDevice.getId())
            .userId(userDevice.getUserId())
            .deviceUniqueId(userDevice.getDeviceUniqueId())
            .fcmToken(userDevice.getFcmToken())
            .osType(userDevice.getOsType())
            .lastActiveAt(userDevice.getLastActiveAt())
            .build();
    }

    public UserDevice toDomain(UserDeviceJpaEntity entity) {
        if (entity == null) return null;

        return UserDevice.builder()
            .id(entity.getId())
            .useId(entity.getUserId())
            .deviceUniqueId(entity.getDeviceUniqueId())
            .fcmToken(entity.getFcmToken())
            .osType(entity.getOsType())
            .localDateTime(entity.getLastActiveAt())
            .build();
    }
}
