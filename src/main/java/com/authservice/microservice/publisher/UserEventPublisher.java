package com.authservice.microservice.publisher;

import com.authservice.microservice.config.RabbitMQConfig;
import com.authservice.microservice.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Publishing UserRegisteredEvent for email: {}", event.getEmail());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USER_REGISTERED,
                event
        );
    }
}