package dalbit.adapter.persistence.jpa.external.audio.mapper;

import dalbit.adapter.persistence.jpa.external.audio.entity.AudioBookJpaEntity;
import dalbit.domain.audio.AudioBook;
import org.springframework.stereotype.Component;

@Component
public class AudioBookJpaMapper {

    public AudioBookJpaEntity toEntity(AudioBook audioBook) {
        if (audioBook == null) return null;

        return AudioBookJpaEntity.builder()
            .id(audioBook.getId())
            .externalId(audioBook.getExternalId())
            .userId(audioBook.getUserId())
            .voiceId(audioBook.getVoiceId())
            .fairytaleId(audioBook.getFairytaleId())
            .status(audioBook.getStatus())
            .audioUrl(audioBook.getAudioUrl())
            .build();
    }

    public AudioBook toDomain(AudioBookJpaEntity entity) {
        if (entity == null) return null;

        return AudioBook.builder()
            .id(entity.getId())
            .externalId(entity.getExternalId())
            .userId(entity.getUserId())
            .voiceId(entity.getVoiceId())
            .fairytaleId(entity.getFairytaleId())
            .status(entity.getStatus())
            .audioUrl(entity.getAudioUrl())
            .build();
    }
}
