package dalbit.application.rest.external.userDevice.service;

import dalbit.application.persistence.jpa.userDevice.port.LoadUserDevicePort;
import dalbit.application.persistence.jpa.userDevice.port.SaveUserDevicePort;
import dalbit.application.rest.external.userDevice.useCase.RegisterUserDeviceUseCase;
import dalbit.application.rest.external.userDevice.useCase.UpdateUserDeviceUseCase;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.userDevice.OsType;
import dalbit.domain.userDevice.UserDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceJpaService implements RegisterUserDeviceUseCase, UpdateUserDeviceUseCase {

    private final LoadUserDevicePort loadUserDevicePort;
    private final SaveUserDevicePort saveUserDevicePort;

    @Override
    @Transactional
    public void registerUseDevice(Long userId, String deviceUniqueId, String fcmToken, OsType osType) {
        UserDevice userDevice = loadUserDevicePort.loadByDeviceUniqueId(deviceUniqueId)
            .orElse(null);

        if (userDevice != null) {
            userDevice.updateTokenAndUser(userId, fcmToken);
            saveUserDevicePort.saveUserDevice(userDevice);
        } else {
            UserDevice newUserDevice = UserDevice.register(userId, deviceUniqueId, fcmToken, osType);
            saveUserDevicePort.saveUserDevice(newUserDevice);
        }
    }

    @Override
    @Transactional
    public void updateToken(Long userId, String deviceUniqueUd, String token) {
        UserDevice userDevice = loadUserDevicePort.loadByUserIdAndDeviceUniqueId(userId, deviceUniqueUd)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_USER_DEVICE));

        userDevice.updateToken(token);

        saveUserDevicePort.saveUserDevice(userDevice);
    }

}

