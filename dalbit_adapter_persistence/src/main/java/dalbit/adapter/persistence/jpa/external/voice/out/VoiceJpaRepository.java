package dalbit.adapter.persistence.jpa.external.voice.out;

import dalbit.adapter.persistence.jpa.external.voice.entity.VoiceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceJpaRepository extends JpaRepository<VoiceJpaEntity, Long> {
    List<VoiceJpaEntity> findAllByIdIn(List<Long> voiceIds);
    List<VoiceJpaEntity> findAllByUserId(Long userId);
    Optional<VoiceJpaEntity> findByExternalId(String externalId);
    Optional<VoiceJpaEntity> findByUserIdAndExternalId(Long userId, String externalId);
    boolean existsByUserIdAndName(Long userId, String name);
    void deleteByUserIdAndExternalId(Long userId, String externalId);
}
