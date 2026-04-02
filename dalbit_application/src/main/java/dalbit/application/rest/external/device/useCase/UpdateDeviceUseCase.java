package dalbit.application.rest.external.device.useCase;

import dalbit.domain.device.DeviceName;

public interface UpdateDeviceUseCase {
    void updateDeviceName(Long userId, String serialNumber, DeviceName newName);
}
