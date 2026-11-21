package com.authservice.microservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "user-exchange";
    public static final String ROUTING_KEY_USER_REGISTERED = "user.registered";

    public static final String ROUTING_KEY_ACCOUNT_DEACTIVATED = "user.account.deactivated";
    public static final String QUEUE_AUTH_ACCOUNT_DEACTIVATED = "auth.account.deactivated.queue";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue authAccountDeactivatedQueue() {
        return new Queue(QUEUE_AUTH_ACCOUNT_DEACTIVATED, true);
    }

    @Bean
    public Binding authAccountDeactivatedBinding(Queue authAccountDeactivatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(authAccountDeactivatedQueue)
                .to(userExchange)
                .with(ROUTING_KEY_ACCOUNT_DEACTIVATED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}