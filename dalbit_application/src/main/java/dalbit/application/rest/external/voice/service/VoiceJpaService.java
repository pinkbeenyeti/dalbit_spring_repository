package dalbit.application.rest.external.voice.service;

import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.GetVoiceInfoUseCase;
import dalbit.application.rest.external.voice.useCase.RegisterVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.UpdateVoiceInfoUseCase;
import dalbit.application.persistence.jpa.voice.port.DeleteVoicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.application.storage.event.StorageDeleteEvent;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.voice.RegistrationStatus;
import dalbit.domain.voice.Voice;
import dalbit.domain.voice.VoiceName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceJpaService implements
    RegisterVoiceUseCase, GetVoiceInfoUseCase, UpdateVoiceInfoUseCase, DeleteVoiceUseCase {

    private final SaveVoicePort saveVoicePort;
    private final LoadVoicePort loadVoicePort;
    private final DeleteVoicePort deleteVoicePort;
    private final GenerateUploadUrlPort generateUploadUrlPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Voice registerVoice(Long userId, String name) {
        if (loadVoicePort.existsByUserIdAndName(userId, name)) {
            throw new DalbitException(ErrorCode.ALREADY_EXIST_VOICE_NAME);
        }

        Voice voice = Voice.register(userId, VoiceName.of(name));

        return saveVoicePort.saveVoice(voice);
    }

    @Override
    @Transactional
    public List<String> getVoiceUploadUrls(Long userId, String targetId, int count) {
        Voice voice = loadVoicePort.loadVoiceByUserIdAndExternalId(userId, targetId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_VOICE));

        List<String> paths = IntStream.rangeClosed(1, count)
            .mapToObj(voice::getRecordFilePath)
            .toList();

        return generateUploadUrlPort.generateUploadUrls(paths);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voice> getVoiceList(Long userId) {
        return loadVoicePort.loadAllVoicesByUserIdAndStatuses(
            userId, 
            List.of(RegistrationStatus.PROCESSING, RegistrationStatus.COMPLETED, RegistrationStatus.FAILED)
        );
    }

    @Override
    @Transactional
    public void updateVoiceName(Long userId, String externalId, String newName) {
        if (loadVoicePort.existsByUserIdAndName(userId, newName)) {
            throw new DalbitException(ErrorCode.ALREADY_EXIST_VOICE_NAME);
        }

        Voice voice = loadVoicePort.loadVoiceByUserIdAndExternalId(userId, externalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_VOICE));
        voice.updateVoiceName(VoiceName.of(newName));
        saveVoicePort.saveVoice(voice);
    }

    @Override
    @Transactional
    public void deleteVoice(Long userId, String externalId) {
        loadVoicePort.loadVoiceByUserIdAndExternalId(userId, externalId).ifPresent(voice -> {
            List<String> directories = new ArrayList<>();
            directories.add(voice.getRecordDirectory());
            
            List<String> files = new ArrayList<>();

            if (voice.getStatus() == RegistrationStatus.COMPLETED) {
                files.add(voice.getModelUrl());
                directories.add(AudioBook.getBaseDirectory(externalId));
            }
            
            eventPublisher.publishEvent(new StorageDeleteEvent(files, directories));
        });

        deleteVoicePort.deleteVoiceByUserIdAndExternalId(userId, externalId);
    }

    @Override
    @Transactional
    public void handleStuckVoices(int retentionHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(retentionHours);

        List<Voice> stuckVoices = loadVoicePort.loadVoicesByStatusInAndCreatedBefore(
            List.of(RegistrationStatus.PROCESSING), threshold);

        if (!stuckVoices.isEmpty()) {
            log.info("[Cleanup] 정체된 목소리 상태 변경 시작 (PROCESSING -> FAILED): {}건", stuckVoices.size());
            stuckVoices.forEach(Voice::failTraining);
            saveVoicePort.saveAllVoices(stuckVoices);
        }
    }

    @Override
    @Transactional
    public void cleanupExpiredVoices(int retentionHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(retentionHours);
        List<RegistrationStatus> targetStatuses = List.of(RegistrationStatus.WAITING_UPLOAD, RegistrationStatus.FAILED);
        List<Voice> expiredVoices = loadVoicePort.loadVoicesByStatusInAndCreatedBefore(targetStatuses, threshold);

        if (!expiredVoices.isEmpty()) {
            log.info("[Cleanup] 만료된 목소리 스토리지 데이터 벌크 삭제 이벤트 발행: {}건", expiredVoices.size());

            List<String> directoriesToDelete = expiredVoices.stream()
                .flatMap(voice -> Stream.of(
                    voice.getRecordDirectory(),
                    AudioBook.getBaseDirectory(voice.getExternalId())
                ))
                .toList();

            eventPublisher.publishEvent(StorageDeleteEvent.ofDirectories(directoriesToDelete));

            List<Long> idsToDelete = expiredVoices.stream()
                .map(Voice::getId)
                .toList();

            deleteVoicePort.deleteAllByIds(idsToDelete);
            log.info("[Cleanup] 만료된 목소리 DB 데이터 삭제 완료: {}건", idsToDelete.size());
        }
    }
}
