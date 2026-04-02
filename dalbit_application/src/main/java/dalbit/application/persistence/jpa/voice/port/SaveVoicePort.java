package dalbit.application.persistence.jpa.voice.port;

import dalbit.domain.voice.Voice;

public interface SaveVoicePort {
    Voice saveVoice(Voice voice);
}
