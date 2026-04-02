package dalbit.adapter.messaging.queue.rabbit.config;

import dalbit.adapter.messaging.queue.rabbit.out.RabbitMqAdapter;
import dalbit.adapter.messaging.queue.rabbit.property.RabbitQueueProperties;
import dalbit.adapter.messaging.queue.rabbit.property.RabbitRouteProperties;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbit_mq")
public class RabbitMqConfig {

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RetryTemplate rabbitRetryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(1000, 2.0, 10000)
            .build();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter,
        RetryTemplate retryTemplate
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        return rabbitTemplate;
    }

    @Bean
    public RabbitMqAdapter rabbitMqdAdapter(RabbitTemplate rabbitTemplate, RabbitRouteProperties properties) {
        return new RabbitMqAdapter(rabbitTemplate, properties);
    }

    @Bean
    public Declarables rabbitMqDeclarables(RabbitQueueProperties queueProps) {
        DirectExchange dlx = new DirectExchange(queueProps.exchangeDlx());

        Queue dlq = QueueBuilder.durable(queueProps.dlq()).build();

        org.springframework.amqp.core.Binding dlqBinding = BindingBuilder
            .bind(dlq)
            .to(dlx)
            .with(queueProps.routingKeyError());

        Queue vtcQueue = QueueBuilder.durable(queueProps.voiceTrainingCompletionQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        Queue agcQueue = QueueBuilder.durable(queueProps.audioBookGenerationCompletionQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        return new Declarables(dlx, dlq, dlqBinding, vtcQueue, agcQueue);
    }
}
