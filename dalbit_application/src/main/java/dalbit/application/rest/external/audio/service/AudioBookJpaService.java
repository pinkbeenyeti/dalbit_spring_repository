package dalbit.application.rest.external.audio.service;


import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.rest.external.audio.useCase.DeleteAudioBookUseCase;
import dalbit.application.rest.external.audio.useCase.GetAudioBooksUseCase;
import dalbit.application.persistence.jpa.audio.port.DeleteAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.LoadAudioBookPort;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AudioBookJpaService implements DeleteAudioBookUseCase, GetAudioBooksUseCase {

    private final LoadVoicePort loadVoicePort;
    private final LoadAudioBookPort loadAudioBookPort;
    private final DeleteAudioBookPort deleteAudioBookPort;

    @Override
    @Transactional(readOnly = true)
    public List<AudioBookResult> getAudioBooks(Long userId) {
        List<AudioBook> audioBookList = loadAudioBookPort.loadAllAudioBookByUserId(userId);

        if (!audioBookList.isEmpty()) {
            List<Long> voiceIds = audioBookList.stream()
                .map(AudioBook::getVoiceId)
                .toList();

            Map<Long, String> voiceExternalIdMap = loadVoicePort.loadExternalIdsByIds(voiceIds);

            return audioBookList.stream()
                .map(audioBook -> new AudioBookResult(
                    audioBook.getExternalId(),
                    voiceExternalIdMap.get(audioBook.getVoiceId()),
                    audioBook.getFairytaleId(),
                    audioBook.getStatus(),
                    audioBook.getAudioUrl()
                    )
                )
                .toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public void deleteAudioBook(Long userId, String audioExternalId) {
        deleteAudioBookPort.deleteAudioBook(userId, audioExternalId);
    }

    @Override
    @Transactional
    public void cleanupExpiredAudioBooks(int retentionHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(retentionHours);

        deleteAudioBookPort.deleteAudioBooksByStatusInAndCreatedBefore(
            List.of(GenerationStatus.FAILED, GenerationStatus.PROCESSING),
            threshold
        );
    }
}
