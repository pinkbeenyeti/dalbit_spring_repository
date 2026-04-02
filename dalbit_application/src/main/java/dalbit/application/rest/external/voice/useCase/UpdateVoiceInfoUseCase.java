package dalbit.application.rest.external.voice.useCase;

public interface UpdateVoiceInfoUseCase {
    void updateVoiceName(Long userId, String externalId, String newName);
}
