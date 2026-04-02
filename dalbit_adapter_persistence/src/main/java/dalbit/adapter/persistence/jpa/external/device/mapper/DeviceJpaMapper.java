package dalbit.adapter.persistence.jpa.external.device.mapper;

import dalbit.adapter.persistence.jpa.external.device.entity.DeviceJpaEntity;
import dalbit.domain.device.Device;
import dalbit.domain.device.DeviceName;
import org.springframework.stereotype.Component;

@Component
public class DeviceJpaMapper {

    public DeviceJpaEntity toEntity(Device device) {
        if (device == null) return null;

        return DeviceJpaEntity.builder()
            .id(device.getId())
            .userId(device.getUserId())
            .serialNumber(device.getSerialNumber())
            .deviceSecret(device.getDeviceSecret())
            .type(device.getType())
            .name(device.getName().getValue())
            .build();
    }

    public Device toDomain(DeviceJpaEntity entity) {
        if (entity == null) return null;

        return Device.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .serialNumber(entity.getSerialNumber())
            .deviceSecret(entity.getDeviceSecret())
            .type(entity.getType())
            .name(DeviceName.of(entity.getName()))
            .build();
    }
}
