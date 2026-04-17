package dalbit.adapter.persistence.jpa.external.audio.out;

import dalbit.adapter.persistence.jpa.external.audio.entity.AudioBookJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;

public interface AudioBookJpaRepository extends JpaRepository<AudioBookJpaEntity, Long> {
    List<AudioBookJpaEntity> findAllByUserId(Long userId);
    Optional<AudioBookJpaEntity> findByExternalId(String externalId);
    void deleteByUserIdAndExternalId(Long userId, String externalId);
    void deleteByStatusInAndCreatedAtBefore(List<GenerationStatus> statuses, LocalDateTime dateTime);
}
