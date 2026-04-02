package dalbit.application.rest.external.device.useCase;

import dalbit.domain.device.Device;
import java.util.List;

public interface GetDevicesUseCase {
    List<Device> getAllDevices(Long userId);
}
