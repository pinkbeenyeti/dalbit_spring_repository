package dalbit.domain.device;

import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Device {

    private final Long id;
    private Long userId;
    private final String serialNumber;
    private final String deviceSecret;
    private final DeviceType type;
    private DeviceName name;

    @Builder
    private Device(Long id, Long userId, String serialNumber, String deviceSecret, DeviceType type, DeviceName name) {
        this.id = id;
        this.userId = userId;
        this.serialNumber = serialNumber;
        this.deviceSecret = deviceSecret;
        this.type = type;
        this.name = name;
    }

    public static Device register(String serialNumber, String deviceSecret, DeviceType type) {
        return new Device(null, null, serialNumber, deviceSecret ,type, DeviceName.of(type.getDescription()));
    }

    public void assignUserId(Long newUserId) {
        if (this.userId != null) throw new DalbitException(ErrorCode.ALREADY_OWNED_DEVICE);
        this.userId = newUserId;
    }

    public void updateName(Long requestUserId, DeviceName newName) {
        validateOwner(requestUserId);
        this.name = newName;
    }

    public boolean verifyDeviceSecret(String requestedSecret) {
        return this.deviceSecret.equals(requestedSecret);
    }

    private void validateOwner(Long requestUserId) {
        if (!requestUserId.equals(this.userId)) {
            throw new DalbitException(ErrorCode.NOT_DEVICE_OWNER);
        }
    }
}
