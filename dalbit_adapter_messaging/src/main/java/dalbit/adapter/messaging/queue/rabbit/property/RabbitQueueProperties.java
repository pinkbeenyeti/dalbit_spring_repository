package dalbit.adapter.messaging.queue.rabbit.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq.queue")
public record RabbitQueueProperties(
    String dlq,
    String exchangeDlx,
    String routingKeyError,
    String voiceTrainingRequestQueue,
    String voiceTrainingCompletionQueue,
    String audiobookGenerationRequestQueue,
    String audiobookGenerationCompletionQueue
) { }