package dalbit.adapter.persistence.jpa.external.audio.out;

import dalbit.adapter.persistence.jpa.external.audio.entity.AudioBookJpaEntity;
import dalbit.adapter.persistence.jpa.external.audio.mapper.AudioBookJpaMapper;
import dalbit.application.persistence.jpa.audio.port.DeleteAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.LoadAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.SaveAudioBookPort;
import dalbit.domain.audio.AudioBook;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AudioBookPersistenceAdapter implements
    SaveAudioBookPort, LoadAudioBookPort, DeleteAudioBookPort {

    private final AudioBookJpaRepository audioBookJpaRepository;
    private final AudioBookJpaMapper audioBookJpaMapper;

    @Override
    public AudioBook saveAudioBook(AudioBook audioBook) {
        AudioBookJpaEntity entity = audioBookJpaRepository.save(audioBookJpaMapper.toEntity(audioBook));
        return audioBookJpaMapper.toDomain(entity);
    }

    @Override
    public List<AudioBook> loadAllAudioBookByUserId(Long userId) {
        return audioBookJpaRepository.findAllByUserId(userId).stream()
            .map(audioBookJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<AudioBook> loadAudioBookByExternalId(String externalId) {
        return audioBookJpaRepository.findByExternalId(externalId)
            .map(audioBookJpaMapper::toDomain);
    }

    @Override
    public void deleteAudioBook(Long userId, String audioExternalId) {
        audioBookJpaRepository.deleteByUserIdAndExternalId(userId, audioExternalId);
    }
}
