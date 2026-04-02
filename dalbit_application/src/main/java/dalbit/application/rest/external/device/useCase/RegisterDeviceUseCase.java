package dalbit.application.rest.external.device.useCase;

import dalbit.domain.device.DeviceType;

public interface RegisterDeviceUseCase {
    void registerNewDevice(String serialNumber, String deviceSecret, DeviceType type);
    void registerDeviceOwner(String serialNumber, Long userId);
}
