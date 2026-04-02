package dalbit.application.rest.external.voice.useCase;

import dalbit.domain.common.storage.Category;
import java.util.List;

public interface RegisterVoiceUseCase {
    void registerVoice(Long userId, String voiceName);
    List<String> getVoiceUploadUrls(Category fileCategory, String targetId, int count);
}
