package dalbit.application.rest.external.voice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import dalbit.application.messaging.queue.dto.VoiceTrainingCompleteEvent;
import dalbit.application.messaging.queue.dto.VoiceTrainingRequestEvent;
import dalbit.application.persistence.jpa.userDevice.port.LoadUserDevicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.application.storage.port.VerifyUploadPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.userDevice.UserDevice;
import dalbit.domain.voice.Voice;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoiceTrainService, 비즈니스 로직 테스트")
class VoiceTrainServiceTest {

    @InjectMocks
    private VoiceTrainService voiceTrainService;

    @Mock private LoadVoicePort loadVoicePort;
    @Mock private SaveVoicePort saveVoicePort;
    @Mock private VerifyUploadPort verifyUploadPort;
    @Mock private LoadUserDevicePort loadUserDevicePort;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Mock private Voice mockVoice;
    @Mock private UserDevice mockUserDevice;

    @Captor private ArgumentCaptor<VoiceTrainingRequestEvent> requestEventCaptor;
    @Captor private ArgumentCaptor<VoiceTrainingCompleteEvent> completeEventCaptor;

    private final Long USER_ID = 1L;
    private final String EXTERNAL_VOICE_ID = "voice-ext-123";
    private final String UPLOAD_PATH = "dalbit/voice/" + EXTERNAL_VOICE_ID + "/audio/";
    private final String MODEL_URL = "https://r2.dummy.com/model.pt";
    private final String FCM_TOKEN = "fcm-token-abc";

    @Nested
    @DisplayName("목소리 학습 로직 테스트")
    class startVoiceTraining {

        @Test
        @DisplayName("목소리 학습 요청 완료: 음성 파일 업로드 확인, DB 업데이트 완료")
        void success_voice_training_request() {
            given(loadVoicePort.loadVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID)).willReturn(Optional.of(mockVoice));
            given(mockVoice.getRecordDirectory()).willReturn(UPLOAD_PATH);
            given(verifyUploadPort.verifyFileCount(UPLOAD_PATH, 10)).willReturn(true);
            given(saveVoicePort.saveVoice(mockVoice)).willReturn(mockVoice);
            given(mockVoice.getExternalId()).willReturn(EXTERNAL_VOICE_ID);

            voiceTrainService.startVoiceTraining(USER_ID, EXTERNAL_VOICE_ID);

            then(mockVoice).should(times(1)).startTraining();
            then(saveVoicePort).should(times(1)).saveVoice(mockVoice);
            then(eventPublisher).should(times(1)).publishEvent(requestEventCaptor.capture());

            VoiceTrainingRequestEvent publishedEvent = requestEventCaptor.getValue();
            assertThat(publishedEvent.voice().getExternalId()).isEqualTo(EXTERNAL_VOICE_ID);
        }

        @Test
        @DisplayName("목소리 학습 요청 실패: 클라이언트에서 DB에 등록되지 않은 Voice External Id 전송")
        void fail_when_voice_not_found() {
            given(loadVoicePort.loadVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> voiceTrainService.startVoiceTraining(USER_ID, EXTERNAL_VOICE_ID))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_VOICE.getMessage());

            then(verifyUploadPort).should(never()).verifyFileCount(anyString(), any(Integer.class));
            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("목소리 학습 요청 실패: 음성 파일 업로드 미완료")
        void fail_when_file_upload_incomplete() {
            given(loadVoicePort.loadVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID)).willReturn(Optional.of(mockVoice));
            given(mockVoice.getRecordDirectory()).willReturn(UPLOAD_PATH);
            given(verifyUploadPort.verifyFileCount(UPLOAD_PATH, 10)).willReturn(false);

            assertThatThrownBy(() -> voiceTrainService.startVoiceTraining(USER_ID, EXTERNAL_VOICE_ID))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.INCOMPLETE_VOICE_UPLOAD.getMessage());

            then(mockVoice).should(never()).startTraining();
            then(saveVoicePort).should(never()).saveVoice(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("목소리 학습 완료 로직 테스트")
    class completeVoiceTraining {

        @Test
        @DisplayName("목소리 학습 완료: FCM 알림 메시지 전송")
        void success_complete_training() {
            given(loadVoicePort.loadVoiceByExternalId(EXTERNAL_VOICE_ID)).willReturn(Optional.of(mockVoice));
            given(saveVoicePort.saveVoice(mockVoice)).willReturn(mockVoice);
            given(mockVoice.getUserId()).willReturn(USER_ID);
            given(mockVoice.getExternalId()).willReturn(EXTERNAL_VOICE_ID);
            given(mockUserDevice.getFcmToken()).willReturn(FCM_TOKEN);
            given(loadUserDevicePort.loadExistFcmTokenUserDevicesByUserId(USER_ID)).willReturn(List.of(mockUserDevice));

            voiceTrainService.completeVoiceTraining(EXTERNAL_VOICE_ID, MODEL_URL);

            then(mockVoice).should(times(1)).completeTraining(MODEL_URL);
            then(saveVoicePort).should(times(1)).saveVoice(mockVoice);
            then(eventPublisher).should(times(1)).publishEvent(completeEventCaptor.capture());

            VoiceTrainingCompleteEvent publishedEvent = completeEventCaptor.getValue();
            assertThat(publishedEvent.userId()).isEqualTo(USER_ID);
            assertThat(publishedEvent.voiceExternalId()).isEqualTo(EXTERNAL_VOICE_ID);
            assertThat(publishedEvent.tokens()).containsExactly(FCM_TOKEN);
        }

        @Test
        @DisplayName("목소리 학습 실패: 큐에서 DB에 등록되지 않은 Voice External Id 전송")
        void fail_not_found_voice_id() {
            given(loadVoicePort.loadVoiceByExternalId(EXTERNAL_VOICE_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> voiceTrainService.completeVoiceTraining(EXTERNAL_VOICE_ID, MODEL_URL))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_VOICE.getMessage());

            then(saveVoicePort).should(never()).saveVoice(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }
    }
}