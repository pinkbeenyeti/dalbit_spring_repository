package dalbit.application.rest.external.audio.service;


import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.rest.external.audio.useCase.DeleteAudioBookUseCase;
import dalbit.application.rest.external.audio.useCase.GetAudioBooksUseCase;
import dalbit.application.persistence.jpa.audio.port.DeleteAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.LoadAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.SaveAudioBookPort;
import dalbit.application.storage.event.StorageDeleteEvent;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.audio.GenerationStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AudioBookJpaService implements DeleteAudioBookUseCase, GetAudioBooksUseCase {

    private final LoadVoicePort loadVoicePort;
    private final LoadAudioBookPort loadAudioBookPort;
    private final DeleteAudioBookPort deleteAudioBookPort;
    private final SaveAudioBookPort saveAudioBookPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<AudioBookResult> getAudioBooks(Long userId) {
        List<AudioBook> audioBookList = loadAudioBookPort.loadAllAudioBookByUserId(userId);

        if (!audioBookList.isEmpty()) {
            List<Long> voiceIds = audioBookList.stream()
                .map(AudioBook::getVoiceId)
                .distinct()
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
        loadAudioBookPort.loadAudioBookByExternalId(audioExternalId).ifPresent(audioBook -> {
            if (audioBook.getStatus() == GenerationStatus.COMPLETED && audioBook.getAudioUrl() != null) {
                eventPublisher.publishEvent(StorageDeleteEvent.ofFile(audioBook.getAudioUrl()));
            }
        });

        deleteAudioBookPort.deleteAudioBook(userId, audioExternalId);
    }

    @Override
    @Transactional
    public void handleStuckAudioBooks(int retentionHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(retentionHours);

        List<AudioBook> stuckAudioBooks = loadAudioBookPort.loadAudioBooksByStatusInAndCreatedBefore(
            List.of(GenerationStatus.PROCESSING), threshold);

        if (!stuckAudioBooks.isEmpty()) {
            log.info("[Cleanup] 정체된 오디오북 상태 변경 시작 (PROCESSING -> FAILED): {}건", stuckAudioBooks.size());
            stuckAudioBooks.forEach(AudioBook::fail);
            saveAudioBookPort.saveAllAudioBooks(stuckAudioBooks);
        }
    }

    @Override
    @Transactional
    public void cleanupExpiredAudioBooks(int retentionHours) {
        LocalDateTime deleteThreshold = LocalDateTime.now().minusHours(retentionHours);
        List<AudioBook> expiredFailedBooks = loadAudioBookPort.loadAudioBooksByStatusInAndCreatedBefore(
            List.of(GenerationStatus.FAILED), deleteThreshold);

        if (!expiredFailedBooks.isEmpty()) {
            log.info("[Cleanup] 만료된 실패 오디오북 스토리지 데이터 삭제 시작: {}건", expiredFailedBooks.size());

            List<String> filesToDelete = expiredFailedBooks.stream()
                .map(AudioBook::getAudioUrl)
                .filter(url -> url != null && !url.isBlank())
                .toList();

            if (!filesToDelete.isEmpty()) {
                eventPublisher.publishEvent(StorageDeleteEvent.ofFiles(filesToDelete));
            }

            List<Long> idsToDelete = expiredFailedBooks.stream().map(AudioBook::getId).toList();
            deleteAudioBookPort.deleteAllByIds(idsToDelete);
            log.info("[Cleanup] 만료된 실패 오디오북 DB 데이터 삭제 완료: {}건", idsToDelete.size());
        }
    }
}
