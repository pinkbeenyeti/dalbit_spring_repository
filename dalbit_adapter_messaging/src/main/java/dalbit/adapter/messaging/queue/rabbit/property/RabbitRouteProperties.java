package dalbit.adapter.messaging.queue.rabbit.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq.route")
public record RabbitRouteProperties(
    String aiExchange,
    String voiceTrainingRoutingKey,
    String audiobookGenerationRoutingKey
) {

}