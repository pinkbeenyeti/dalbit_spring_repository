package dalbit.adapter.messaging.queue.rabbit.config;

import dalbit.adapter.messaging.queue.rabbit.out.RabbitMqAdapter;
import dalbit.adapter.messaging.queue.rabbit.property.RabbitQueueProperties;
import dalbit.adapter.messaging.queue.rabbit.property.RabbitRouteProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
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
    public Declarables rabbitMqDeclarables(RabbitQueueProperties queueProps, RabbitRouteProperties routeProps) {
        // 1. Exchanges
        TopicExchange requestExchange = new TopicExchange(routeProps.requestExchange());
        TopicExchange responseExchange = new TopicExchange(routeProps.responseExchange());
        DirectExchange dlx = new DirectExchange(queueProps.exchangeDlx());

        // 2. Queues
        Queue dlq = QueueBuilder.durable(queueProps.dlq()).build();

        // AI Request Queues
        Queue vtrQueue = QueueBuilder.durable(queueProps.voiceTrainingRequestQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        Queue agrQueue = QueueBuilder.durable(queueProps.audiobookGenerationRequestQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        // Spring Completion Queues
        Queue vtcQueue = QueueBuilder.durable(queueProps.voiceTrainingCompletionQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        Queue agcQueue = QueueBuilder.durable(queueProps.audiobookGenerationCompletionQueue())
            .withArgument("x-dead-letter-exchange", queueProps.exchangeDlx())
            .withArgument("x-dead-letter-routing-key", queueProps.routingKeyError())
            .build();

        // 3. Bindings
        Binding dlqBinding = BindingBuilder.bind(dlq).to(dlx).with(queueProps.routingKeyError());

        // Request Bindings
        Binding vtrBinding = BindingBuilder.bind(vtrQueue).to(requestExchange).with(routeProps.voiceTrainingRequestRoutingKey());
        Binding agrBinding = BindingBuilder.bind(agrQueue).to(requestExchange).with(routeProps.audiobookGenerationRequestRoutingKey());

        // Response Bindings
        Binding vtcBinding = BindingBuilder.bind(vtcQueue).to(responseExchange).with(routeProps.voiceTrainingResponseRoutingKey());
        Binding agcBinding = BindingBuilder.bind(agcQueue).to(responseExchange).with(routeProps.audiobookGenerationResponseRoutingKey());

        return new Declarables(
            requestExchange, responseExchange, dlx,
            dlq, vtrQueue, agrQueue, vtcQueue, agcQueue,
            dlqBinding, vtrBinding, agrBinding, vtcBinding, agcBinding
        );
    }
}
