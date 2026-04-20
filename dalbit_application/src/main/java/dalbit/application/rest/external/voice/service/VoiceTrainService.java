package dalbit.application.rest.external.voice.service;

import dalbit.application.messaging.queue.dto.VoiceTrainingCompleteEvent;
import dalbit.application.messaging.queue.dto.VoiceTrainingRequestEvent;
import dalbit.application.persistence.jpa.userDevice.port.LoadUserDevicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.application.rest.external.voice.useCase.TrainVoiceUseCase;
import dalbit.application.storage.port.VerifyUploadPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.userDevice.UserDevice;
import dalbit.domain.voice.Voice;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceTrainService implements TrainVoiceUseCase {

    private final LoadVoicePort loadVoicePort;
    private final SaveVoicePort saveVoicePort;
    private final VerifyUploadPort verifyUploadPort;
    private final LoadUserDevicePort loadUserDevicePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void startVoiceTraining(Long userId, String externalId) {
        Voice voice = loadVoicePort.loadVoiceByUserIdAndExternalId(userId, externalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_VOICE));

        if (!verifyUploadPort.verifyFileCount(voice.getRecordDirectory(), 10)) {
            log.warn("음성 파일 10개가 모두 업로드되지 않았습니다. 학습 요청 거부 - externalId: {}, path: {}", externalId, voice.getRecordDirectory());
            throw new DalbitException(ErrorCode.INCOMPLETE_VOICE_UPLOAD);
        }

        voice.startTraining();
        Voice savedVoice = saveVoicePort.saveVoice(voice);

        eventPublisher.publishEvent(new VoiceTrainingRequestEvent(savedVoice));
    }

    @Override
    @Transactional
    public void completeVoiceTraining(String externalId, String modelUrl, boolean isSuccess) {
        Voice voice = loadVoicePort.loadVoiceByExternalId(externalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_VOICE));

        if (isSuccess)  voice.completeTraining(modelUrl);
        else {
            log.warn("[AI Server] 목소리 학습 실패 보고 수신 - externalId: {}", externalId);
            voice.failTraining();
        }

        Voice savedVoice = saveVoicePort.saveVoice(voice);

        List<UserDevice> userDevices = loadUserDevicePort.loadExistFcmTokenUserDevicesByUserId(savedVoice.getUserId());
        List<String> tokens = userDevices.stream()
                .map(UserDevice::getFcmToken)
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new VoiceTrainingCompleteEvent(
            savedVoice.getUserId(),
            savedVoice.getExternalId(),
            tokens,
            isSuccess));
    }
}
