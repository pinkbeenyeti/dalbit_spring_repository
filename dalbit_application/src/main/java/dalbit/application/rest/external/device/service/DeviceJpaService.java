package dalbit.application.rest.external.device.service;

import dalbit.application.rest.external.device.useCase.GetDevicesUseCase;
import dalbit.application.rest.external.device.useCase.RegisterDeviceUseCase;
import dalbit.application.rest.external.device.useCase.UpdateDeviceUseCase;
import dalbit.application.persistence.jpa.device.port.LoadDevicePort;
import dalbit.application.persistence.jpa.device.port.SaveDevicePort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.device.Device;
import dalbit.domain.device.DeviceName;
import dalbit.domain.device.DeviceType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceJpaService implements GetDevicesUseCase, RegisterDeviceUseCase, UpdateDeviceUseCase {

    private final LoadDevicePort loadDevicePort;
    private final SaveDevicePort saveDevicePort;

    @Override
    @Transactional(readOnly = true)
    public List<Device> getAllDevices(Long userId) {
        return loadDevicePort.loadAllDevicesByUserId(userId);
    }

    @Override
    @Transactional
    public void registerNewDevice(String serialNumber, String deviceSecret, DeviceType type) {
        Device device = loadDevicePort.loadDeviceBySerialNumber(serialNumber)
            .orElse(null);

        if (device != null){
            throw new DalbitException(ErrorCode.ALREADY_EXIST_DEVICE);
        }

        device = Device.register(serialNumber, deviceSecret, type);
        saveDevicePort.saveDevice(device);
    }

    @Override
    @Transactional
    public void registerDeviceOwner(String serialNumber, Long userId) {
        Device device = loadDevicePort.loadDeviceBySerialNumber(serialNumber)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_DEVICE));
        device.assignUserId(userId);
        saveDevicePort.saveDevice(device);
    }

    @Override
    @Transactional
    public void updateDeviceName(Long userId, String serialNumber, DeviceName newName) {
        Device device = loadDevicePort.loadDeviceBySerialNumber(serialNumber)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_DEVICE));
        device.updateName(userId, newName);
        saveDevicePort.saveDevice(device);
    }

}
