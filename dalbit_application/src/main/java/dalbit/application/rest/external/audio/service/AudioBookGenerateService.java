package dalbit.application.rest.external.audio.service;

import dalbit.application.messaging.queue.dto.AudioBookGenerationCompleteEvent;
import dalbit.application.messaging.queue.dto.AudioBookGenerationRequestEvent;
import dalbit.application.persistence.jpa.audio.port.LoadAudioBookPort;
import dalbit.application.persistence.jpa.audio.port.SaveAudioBookPort;
import dalbit.application.persistence.jpa.fairytale.port.LoadFairytalePort;
import dalbit.application.persistence.jpa.userDevice.port.LoadUserDevicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.rest.external.audio.useCase.GenerateAudioBookUseCase;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.fairytale.Fairytale;
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
public class AudioBookGenerateService implements GenerateAudioBookUseCase {


    private final LoadVoicePort loadVoicePort;
    private final LoadFairytalePort loadFairytalePort;
    private final LoadUserDevicePort loadUserDevicePort;
    private final LoadAudioBookPort loadAudioBookPort;
    private final SaveAudioBookPort saveAudioBookPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void startGenerateAudioBook(Long userId, Long fairytaleId, String voiceExternalId) {
        Voice voice = loadVoicePort.loadVoiceByUserIdAndExternalId(userId, voiceExternalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_VOICE));

        Fairytale fairytale = loadFairytalePort.loadFairytaleById(fairytaleId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_FAIRYTALE));

        AudioBook audioBook = AudioBook.generate(userId, fairytaleId, voice.getId());
        AudioBook savedAudioBook = saveAudioBookPort.saveAudioBook(audioBook);

        eventPublisher.publishEvent(new AudioBookGenerationRequestEvent(
            savedAudioBook.getExternalId(),
            voiceExternalId,
            fairytale.getContentUrl()
        ));
    }

    @Override
    @Transactional
    public void completeGenerateAudioBook(String audioBookExternalId, String audioUrl, String status) {
        AudioBook audioBook = loadAudioBookPort.loadAudioBookByExternalId(audioBookExternalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_AUDIO_BOOK));

        if (status.equalsIgnoreCase("FAIL")) {
            log.error("[AI Server] 오디오북 생성 실패 보고 수신 - audiobook_externalId: {}", audioBookExternalId);

            audioBook.fail();
            saveAudioBookPort.saveAudioBook(audioBook);

            return;
        }

        audioBook.complete(audioUrl);
        AudioBook savedAudioBook = saveAudioBookPort.saveAudioBook(audioBook);

        List<UserDevice> userDevices = loadUserDevicePort.loadExistFcmTokenUserDevicesByUserId(savedAudioBook.getUserId());
        List<String> tokens = userDevices.stream()
                .map(UserDevice::getFcmToken)
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new AudioBookGenerationCompleteEvent(audioBook.getUserId(), audioBookExternalId, tokens));
    }
}
