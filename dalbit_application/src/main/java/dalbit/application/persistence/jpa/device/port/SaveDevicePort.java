package dalbit.application.persistence.jpa.device.port;

import dalbit.domain.device.Device;

public interface SaveDevicePort {
    Device saveDevice(Device device);
}
