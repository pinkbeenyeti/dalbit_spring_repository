package dalbit.application.rest.external.audio.useCase;

public interface DeleteAudioBookUseCase {
    void deleteAudioBook(Long userId, String audioExternalId);
}
