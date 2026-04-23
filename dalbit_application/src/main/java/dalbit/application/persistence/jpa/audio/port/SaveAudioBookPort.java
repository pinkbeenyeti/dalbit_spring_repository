package dalbit.application.persistence.jpa.audio.port;

import dalbit.domain.audio.AudioBook;
import java.util.List;

public interface SaveAudioBookPort {
    AudioBook saveAudioBook(AudioBook audioBook);
    void saveAllAudioBooks(List<AudioBook> audioBooks);
}
