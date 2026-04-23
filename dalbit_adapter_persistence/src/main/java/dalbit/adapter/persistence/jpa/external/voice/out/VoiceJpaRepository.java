package dalbit.adapter.persistence.jpa.external.voice.out;

import dalbit.adapter.persistence.jpa.external.voice.entity.VoiceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dalbit.domain.voice.RegistrationStatus;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoiceJpaRepository extends JpaRepository<VoiceJpaEntity, Long> {
    List<VoiceJpaEntity> findAllByIdIn(List<Long> voiceIds);
    List<VoiceJpaEntity> findAllByUserIdAndStatusIn(Long userId, List<RegistrationStatus> statuses);
    List<VoiceJpaEntity> findAllByStatusInAndCreatedAtBefore(List<RegistrationStatus> statuses, LocalDateTime dateTime);
    Optional<VoiceJpaEntity> findByExternalId(String externalId);
    Optional<VoiceJpaEntity> findByUserIdAndExternalId(Long userId, String externalId);
    boolean existsByUserIdAndName(Long userId, String name);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from VoiceJpaEntity v where v.id in :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);
    void deleteByUserIdAndExternalId(Long userId, String externalId);
}
