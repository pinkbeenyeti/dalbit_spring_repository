package dalbit.application.rest.external.voice.useCase;

public interface TrainVoiceUseCase {
    void startVoiceTraining(Long userId, String externalId);
    void completeVoiceTraining(String externalId, String modelUrl, boolean isSuccess);
}
