package dalbit.application.rest.external.voice.service;

import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.GetVoiceInfoUseCase;
import dalbit.application.rest.external.voice.useCase.RegisterVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.UpdateVoiceInfoUseCase;
import dalbit.application.persistence.jpa.voice.port.DeleteVoicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.voice.RegistrationStatus;
import dalbit.domain.voice.Voice;
import dalbit.domain.voice.VoiceName;
import java.util.List;
import java.util.stream.IntStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoiceJpaService implements
    RegisterVoiceUseCase, GetVoiceInfoUseCase, UpdateVoiceInfoUseCase, DeleteVoiceUseCase {

    private final SaveVoicePort saveVoicePort;
    private final LoadVoicePort loadVoicePort;
    private final DeleteVoicePort deleteVoicePort;
    private final GenerateUploadUrlPort generateUploadUrlPort;

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
        deleteVoicePort.deleteVoiceByUserIdAndExternalId(userId, externalId);
    }

    @Override
    @Transactional
    public void cleanupExpiredVoices(int retentionHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(retentionHours);

        deleteVoicePort.deleteVoicesByStatusInAndCreatedBefore(
            List.of(RegistrationStatus.WAITING_UPLOAD, RegistrationStatus.FAILED, RegistrationStatus.PROCESSING),
            threshold
        );
    }
}
