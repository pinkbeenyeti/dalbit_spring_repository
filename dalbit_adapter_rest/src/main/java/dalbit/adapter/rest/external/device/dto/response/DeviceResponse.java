package dalbit.adapter.rest.external.device.dto.response;

import dalbit.domain.device.Device;

public record DeviceResponse(
    String serialNumber,
    String deviceType,
    String name
) {

    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
            device.getSerialNumber(),
            device.getType().getDescription(),
            device.getName().getValue()
        );
    }
}
