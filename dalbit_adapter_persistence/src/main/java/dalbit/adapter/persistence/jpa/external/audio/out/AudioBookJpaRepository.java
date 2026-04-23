package dalbit.adapter.persistence.jpa.external.audio.out;

import dalbit.adapter.persistence.jpa.external.audio.entity.AudioBookJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AudioBookJpaRepository extends JpaRepository<AudioBookJpaEntity, Long> {
    List<AudioBookJpaEntity> findAllByUserId(Long userId);
    List<AudioBookJpaEntity> findAllByStatusInAndCreatedAtBefore(List<GenerationStatus> statuses, LocalDateTime dateTime);
    Optional<AudioBookJpaEntity> findByExternalId(String externalId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from AudioBookJpaEntity a where a.id in :ids")
    void deleteAllByIdIn(@org.springframework.data.repository.query.Param("ids") List<Long> ids);
    void deleteByUserIdAndExternalId(Long userId, String externalId);
}
