package dalbit.application.rest.external.audio.useCase;

public interface DeleteAudioBookUseCase {
    void deleteAudioBook(Long userId, String audioExternalId);
    void handleStuckAudioBooks(int retentionHours);
    void cleanupExpiredAudioBooks(int retentionHours);
}
