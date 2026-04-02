package dalbit.adapter.persistence.jpa.external.device.out;

import dalbit.adapter.persistence.jpa.external.device.entity.DeviceJpaEntity;
import dalbit.adapter.persistence.jpa.external.device.mapper.DeviceJpaMapper;
import dalbit.application.persistence.jpa.device.port.LoadDevicePort;
import dalbit.application.persistence.jpa.device.port.SaveDevicePort;
import dalbit.domain.device.Device;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevicePersistenceAdapter implements SaveDevicePort, LoadDevicePort {

    private final DeviceJpaRepository deviceJpaRepository;
    private final DeviceJpaMapper deviceJpaMapper;

    @Override
    public Device saveDevice(Device device) {
        DeviceJpaEntity entity = deviceJpaRepository.save(deviceJpaMapper.toEntity(device));
        return deviceJpaMapper.toDomain(entity);
    }

    @Override
    public Optional<Device> loadDeviceBySerialNumber(String serialNumber) {
        return deviceJpaRepository.findBySerialNumber(serialNumber)
            .map(deviceJpaMapper::toDomain);
    }

    @Override
    public List<Device> loadAllDevicesByUserId(Long userId) {
        return deviceJpaRepository.findAllByUserId(userId).stream()
            .map(deviceJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

}
