package dalbit.adapter.persistence.jpa.external.voice.out;

import dalbit.adapter.persistence.jpa.external.voice.entity.VoiceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dalbit.domain.voice.RegistrationStatus;

import java.time.LocalDateTime;

public interface VoiceJpaRepository extends JpaRepository<VoiceJpaEntity, Long> {
    List<VoiceJpaEntity> findAllByIdIn(List<Long> voiceIds);
    List<VoiceJpaEntity> findAllByUserIdAndStatusIn(Long userId, List<RegistrationStatus> statuses);
    Optional<VoiceJpaEntity> findByExternalId(String externalId);
    Optional<VoiceJpaEntity> findByUserIdAndExternalId(Long userId, String externalId);
    boolean existsByUserIdAndName(Long userId, String name);
    void deleteByUserIdAndExternalId(Long userId, String externalId);
    void deleteByStatusInAndCreatedAtBefore(List<RegistrationStatus> statuses, LocalDateTime dateTime);
}
