package dalbit.application.persistence.jpa.audio.port;

import dalbit.domain.audio.AudioBook;
import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadAudioBookPort {
    List<AudioBook> loadAllAudioBookByUserId(Long userId);
    List<AudioBook> loadAudioBooksByStatusInAndCreatedBefore(List<GenerationStatus> statuses, LocalDateTime dateTime);
    Optional<AudioBook> loadAudioBookByExternalId(String externalId);
}
