package dalbit.application.persistence.jpa.voice.port;

import dalbit.domain.voice.RegistrationStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface DeleteVoicePort {
    void deleteVoiceByUserIdAndExternalId(Long userId, String externalId);
    void deleteAllByIds(List<Long> ids);
}
