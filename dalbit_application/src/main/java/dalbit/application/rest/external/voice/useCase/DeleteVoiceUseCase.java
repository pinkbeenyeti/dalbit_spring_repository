package dalbit.application.rest.external.voice.useCase;

public interface DeleteVoiceUseCase {
    void deleteVoice(Long userId, String externalId);
    void handleStuckVoices(int retentionHours);
    void cleanupExpiredVoices(int retentionHours);
}
