package dalbit.application.persistence.jpa.audio.port;

public interface DeleteAudioBookPort {
    void deleteAudioBook(Long userId, String externalId);
}
