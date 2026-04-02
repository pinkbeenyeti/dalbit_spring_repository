package dalbit.application.persistence.jpa.audio.port;

import dalbit.domain.audio.AudioBook;

public interface SaveAudioBookPort {
    AudioBook saveAudioBook(AudioBook audioBook);
}
