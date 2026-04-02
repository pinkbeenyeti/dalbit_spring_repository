package dalbit.application.persistence.jpa.voice.port;

import dalbit.domain.voice.Voice;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoadVoicePort {
    List<Voice> loadAllVoicesByUserId(Long userId);
    Optional<Voice> loadVoiceByExternalId(String externalId);
    Optional<Voice> loadVoiceByUserIdAndExternalId(Long userId, String externalId);
    Map<Long, String> loadExternalIdsByIds(List<Long> voiceIds);
    boolean existsByUserIdAndName(Long userId, String name);
}
