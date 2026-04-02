package dalbit.application.persistence.jpa.device.port;

import dalbit.domain.device.Device;
import java.util.List;
import java.util.Optional;

public interface LoadDevicePort {
    Optional<Device> loadDeviceBySerialNumber(String serialNumber);
    List<Device> loadAllDevicesByUserId(Long userId);
}
