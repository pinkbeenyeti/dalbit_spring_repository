package dalbit.application.persistence.jpa.audio.port;

import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface DeleteAudioBookPort {
    void deleteAudioBook(Long userId, String externalId);
    void deleteAudioBooksByStatusInAndCreatedBefore(List<GenerationStatus> statuses, LocalDateTime dateTime);
}
