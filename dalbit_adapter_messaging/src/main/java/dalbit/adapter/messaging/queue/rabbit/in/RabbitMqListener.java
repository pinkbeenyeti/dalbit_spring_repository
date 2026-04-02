package dalbit.adapter.messaging.queue.rabbit.in;

import dalbit.adapter.messaging.queue.rabbit.payload.AudioBookGenerateCompletePayload;
import dalbit.adapter.messaging.queue.rabbit.payload.VoiceTrainCompletePayload;
import dalbit.application.rest.external.audio.useCase.GenerateAudioBookUseCase;
import dalbit.application.rest.external.voice.useCase.TrainVoiceUseCase;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbit_mq")
public class RabbitMqListener {

    private final TrainVoiceUseCase trainVoiceUseCase;
    private final GenerateAudioBookUseCase generateAudioBookUseCase;

    @RabbitListener(
        queues = "${rabbitmq.queue.voice-training-completion-queue}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void listenVoiceTrainingCompletionMessage(
        VoiceTrainCompletePayload payload,
        Channel channel,
        Message message
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("[RabbitMQ] 목소리 학습 완료 메시지 수신 - voice_externalId: {}", payload.voiceExternalId());

        try {
            trainVoiceUseCase.completeVoiceTraining(payload.voiceExternalId(), payload.voiceModelUrl());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[RabbitMQ] 목소리 학습 완료 처리 중 비즈니스 에러 발생 - voice_externalId: {}, error: {}", payload.voiceExternalId(), e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
        queues = "${rabbitmq.queue.audioBook-generation-completion-queue}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void listenAudioBookGenerationCompletionMessage(
        AudioBookGenerateCompletePayload payload,
        Channel channel,
        Message message
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("[RabbitMQ] 오디오북 생성 완료 메시지 수신 - audioBook_externalId: {}", payload.audioBookExternalId());

        try {
            generateAudioBookUseCase.completeGenerateAudioBook(payload.audioBookExternalId(), payload.audioBookAudioUrl(), payload.audioBookGenerationStatus());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[RabbitMQ] 오디오북 생성 완료 처리 중 비즈니스 에러 발생 - audioBook_externalId: {}, error: {}", payload.audioBookExternalId(), e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

}