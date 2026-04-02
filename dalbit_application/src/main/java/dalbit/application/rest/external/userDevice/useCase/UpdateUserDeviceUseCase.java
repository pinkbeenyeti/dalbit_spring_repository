package dalbit.application.rest.external.userDevice.useCase;

public interface UpdateUserDeviceUseCase {
    void updateToken(Long userId, String deviceUniqueId, String token);
}
