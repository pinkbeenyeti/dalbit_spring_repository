package dalbit.application.persistence.jpa.voice.port;

import dalbit.domain.voice.Voice;
import java.util.List;

public interface SaveVoicePort {
    Voice saveVoice(Voice voice);
    void saveAllVoices(List<Voice> voices);
}
