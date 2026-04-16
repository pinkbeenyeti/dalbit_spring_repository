package dalbit.adapter.persistence.jpa.external.voice.mapper;

import dalbit.adapter.persistence.jpa.external.voice.entity.VoiceJpaEntity;
import dalbit.domain.voice.Voice;
import dalbit.domain.voice.VoiceName;
import org.springframework.stereotype.Component;

@Component
public class VoiceJpaMapper {
public VoiceJpaEntity toEntity(Voice voice) {
    if (voice == null) return null;

    return VoiceJpaEntity.builder()
        .id(voice.getId())
        .userId(voice.getUserId())
        .externalId(voice.getExternalId())
        .status(voice.getStatus())
        .name(voice.getName().getValue())
        .modelUrl(voice.getModelUrl())
        .createdAt(voice.getCreatedAt())
        .build();
}

public Voice toDomain(VoiceJpaEntity entity) {
    if (entity == null) return null;

    return Voice.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .externalId(entity.getExternalId())
        .status(entity.getStatus())
        .name(VoiceName.of(entity.getName()))
        .modelUrl(entity.getModelUrl())
        .createdAt(entity.getCreatedAt())
        .build();
}

}
