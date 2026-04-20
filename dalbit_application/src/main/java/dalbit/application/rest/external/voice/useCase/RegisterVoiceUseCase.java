package dalbit.application.rest.external.voice.useCase;

import dalbit.domain.voice.Voice;
import java.util.List;

public interface RegisterVoiceUseCase {
    Voice registerVoice(Long userId, String voiceName);
    List<String> getVoiceUploadUrls(Long userId, String targetId, int count);
}
