package dalbit.application.rest.external.voice.useCase;

import dalbit.domain.voice.Voice;
import java.util.List;

public interface GetVoiceInfoUseCase {
    List<Voice> getVoiceList(Long userId);
}
