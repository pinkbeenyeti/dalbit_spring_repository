package dalbit.adapter.messaging.queue.rabbit.out;

import dalbit.adapter.messaging.queue.rabbit.payload.AudioBookGenerateRequestPayload;
import dalbit.adapter.messaging.queue.rabbit.payload.VoiceTrainingRequestPayload;
import dalbit.adapter.messaging.queue.rabbit.property.RabbitRouteProperties;
import dalbit.application.messaging.queue.port.SendAudioBookGeneratePort;
import dalbit.application.messaging.queue.port.SendVoiceTrainingPort;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.voice.Voice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
@RequiredArgsConstructor
public class RabbitMqAdapter implements SendVoiceTrainingPort, SendAudioBookGeneratePort {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitRouteProperties properties;

    @Override
    public void sendVoiceTrainingRequest(Voice voice) {
        VoiceTrainingRequestPayload payload = VoiceTrainingRequestPayload.from(voice);

        try {
            log.info("[RabbitMQ] 목소리 학습 요청 전송 - 목소리 외부 아이디: {}, 페이로드: {}", voice.getExternalId(), payload);
            rabbitTemplate.convertAndSend(properties.requestExchange(), properties.voiceTrainingRequestRoutingKey(), payload);
        } catch (Exception e) {
            log.error("[RabbitMQ] 목소리 학습 요청 전송 실패 - 목소리 외부 아이디: {}, 페이로드: {}", voice.getExternalId(), payload, e);
            throw new DalbitException(ErrorCode.FAIL_SEND_COMMAND);
        }
    }

    @Override
    public void sendAudioBookGenerateRequest(AudioBook audioBook, Voice voice, Long fairytaleId) {
        AudioBookGenerateRequestPayload payload = AudioBookGenerateRequestPayload.from(audioBook, voice, fairytaleId);

        try {
            log.info("[RabbitMQ] 오디오북 생성 요청 전송 - 오디오북 외부 아이디: {}", audioBook.getExternalId());
            rabbitTemplate.convertAndSend(properties.requestExchange(), properties.audiobookGenerationRequestRoutingKey(), payload);
        } catch (Exception e) {
            log.error("[RabbitMQ] 오디오북 생성 요청 전송 실패 - 오디오북 외부 아이디: {}, 에러: {}", audioBook.getExternalId(), e.getMessage());
            throw new DalbitException(ErrorCode.FAIL_SEND_COMMAND);
        }
    }
}
