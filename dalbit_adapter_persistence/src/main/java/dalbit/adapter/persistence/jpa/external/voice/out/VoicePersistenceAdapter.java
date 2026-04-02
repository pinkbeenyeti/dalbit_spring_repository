package dalbit.adapter.persistence.jpa.external.voice.out;

import dalbit.adapter.persistence.jpa.external.voice.entity.VoiceJpaEntity;
import dalbit.adapter.persistence.jpa.external.voice.mapper.VoiceJpaMapper;
import dalbit.application.persistence.jpa.voice.port.DeleteVoicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.domain.voice.Voice;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoicePersistenceAdapter implements SaveVoicePort, LoadVoicePort, DeleteVoicePort {

    private final VoiceJpaRepository voiceJpaRepository;
    private final VoiceJpaMapper voiceJpaMapper;

    @Override
    public Voice saveVoice(Voice voice) {
        VoiceJpaEntity entity = voiceJpaRepository.save(voiceJpaMapper.toEntity(voice));
        return voiceJpaMapper.toDomain(entity);
    }

    @Override
    public List<Voice> loadAllVoicesByUserId(Long userId) {
        return voiceJpaRepository.findAllByUserId(userId).stream()
            .map(voiceJpaMapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Voice> loadVoiceByExternalId(String externalId) {
        return voiceJpaRepository.findByExternalId(externalId)
            .map(voiceJpaMapper::toDomain);
    }

    @Override
    public Optional<Voice> loadVoiceByUserIdAndExternalId(Long userId, String externalId) {
        return voiceJpaRepository.findByUserIdAndExternalId(userId, externalId)
            .map(voiceJpaMapper::toDomain);
    }

    @Override
    public Map<Long, String> loadExternalIdsByIds(List<Long> voiceIds) {
        List<VoiceJpaEntity> voiceEntityList = voiceJpaRepository.findAllByIdIn(voiceIds);
        return voiceEntityList.stream()
            .collect(Collectors.toMap(VoiceJpaEntity::getId, VoiceJpaEntity::getExternalId));
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return voiceJpaRepository.existsByUserIdAndName(userId, name);
    }

    @Override
    public void deleteVoiceByUserIdAndExternalId(Long userId, String externalId) {
        voiceJpaRepository.deleteByUserIdAndExternalId(userId, externalId);
    }
}
