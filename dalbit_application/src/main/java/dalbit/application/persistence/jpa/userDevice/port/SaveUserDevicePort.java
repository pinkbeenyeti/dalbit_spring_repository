package dalbit.application.persistence.jpa.userDevice.port;

import dalbit.domain.userDevice.UserDevice;

public interface SaveUserDevicePort {
    UserDevice saveUserDevice(UserDevice userDevice);
}
