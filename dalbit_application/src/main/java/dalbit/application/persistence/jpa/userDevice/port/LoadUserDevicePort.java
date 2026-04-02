package dalbit.application.persistence.jpa.userDevice.port;

import dalbit.domain.userDevice.UserDevice;
import java.util.List;
import java.util.Optional;

public interface LoadUserDevicePort {
    List<UserDevice> loadAllUserDeviceByUserId(Long userId);
    List<UserDevice> loadExistFcmTokenUserDevicesByUserId(Long userId);
    Optional<UserDevice> loadByDeviceUniqueId(String deviceUniqueId);
    Optional<UserDevice> loadByUserIdAndDeviceUniqueId(Long userId, String deviceUniqueId);
}
