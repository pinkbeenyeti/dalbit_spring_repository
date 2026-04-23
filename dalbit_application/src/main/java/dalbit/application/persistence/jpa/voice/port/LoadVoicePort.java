package dalbit.application.persistence.jpa.voice.port;

import dalbit.domain.voice.RegistrationStatus;
import dalbit.domain.voice.Voice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoadVoicePort {
    List<Voice> loadAllVoicesByUserIdAndStatuses(Long userId, List<RegistrationStatus> statuses);
    List<Voice> loadVoicesByStatusInAndCreatedBefore(List<RegistrationStatus> statuses, LocalDateTime dateTime);
    Optional<Voice> loadVoiceByExternalId(String externalId);
    Optional<Voice> loadVoiceByUserIdAndExternalId(Long userId, String externalId);
    Map<Long, String> loadExternalIdsByIds(List<Long> voiceIds);
    boolean existsByUserIdAndName(Long userId, String name);
}
