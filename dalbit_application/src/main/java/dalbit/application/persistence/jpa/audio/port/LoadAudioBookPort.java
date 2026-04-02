package dalbit.application.persistence.jpa.audio.port;

import dalbit.domain.audio.AudioBook;
import java.util.List;
import java.util.Optional;

public interface LoadAudioBookPort {
    List<AudioBook> loadAllAudioBookByUserId(Long userId);
    Optional<AudioBook> loadAudioBookByExternalId(String externalId);
}
