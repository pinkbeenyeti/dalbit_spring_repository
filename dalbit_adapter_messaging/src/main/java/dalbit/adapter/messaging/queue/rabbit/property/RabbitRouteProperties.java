package dalbit.adapter.messaging.queue.rabbit.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq.route")
public record RabbitRouteProperties(
    String requestExchange,
    String responseExchange,
    String voiceTrainingRequestRoutingKey,
    String voiceTrainingResponseRoutingKey,
    String audiobookGenerationRequestRoutingKey,
    String audiobookGenerationResponseRoutingKey
) {

}