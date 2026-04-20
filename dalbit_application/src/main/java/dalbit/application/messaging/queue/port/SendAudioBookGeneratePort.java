package dalbit.application.messaging.queue.port;

import dalbit.domain.audio.AudioBook;
import dalbit.domain.voice.Voice;

public interface SendAudioBookGeneratePort {
    void sendAudioBookGenerateRequest(AudioBook audioBook, Voice voice, Long fairytaleId);
}
