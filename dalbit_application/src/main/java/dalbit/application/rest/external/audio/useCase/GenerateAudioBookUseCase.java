package dalbit.application.rest.external.audio.useCase;

public interface GenerateAudioBookUseCase {
    void startGenerateAudioBook(Long userId, Long fairytaleId, String voiceExternalId);
    void completeGenerateAudioBook(String audioBookExternalId, String audioUrl, boolean isSuccess);
}
