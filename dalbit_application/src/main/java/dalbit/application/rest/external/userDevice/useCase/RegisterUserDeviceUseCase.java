package dalbit.application.rest.external.userDevice.useCase;

import dalbit.domain.userDevice.OsType;

public interface RegisterUserDeviceUseCase {
    void registerUseDevice(Long userId, String deviceUniqueId, String fcmToken, OsType osType);
}
