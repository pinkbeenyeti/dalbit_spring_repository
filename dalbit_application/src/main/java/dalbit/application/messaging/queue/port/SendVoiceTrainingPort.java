package dalbit.application.messaging.queue.port;

import dalbit.domain.voice.Voice;

public interface SendVoiceTrainingPort {
    void sendVoiceTrainingRequest(Voice voice);
}
