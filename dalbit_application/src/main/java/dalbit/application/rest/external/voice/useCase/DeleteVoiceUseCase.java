package dalbit.application.rest.external.voice.useCase;

public interface DeleteVoiceUseCase {
    void deleteVoice(Long userId, String externalId);
}
