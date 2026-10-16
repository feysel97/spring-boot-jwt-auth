package com.authservice.microservice.publisher;

import com.authservice.microservice.config.RabbitMQConfig;
import com.authservice.microservice.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEvent(String routingKey, NotificationEvent event) {
        log.info("Publishing {} for email: {} with routing key: {}",
                event.getEventType(), event.getEmail(), routingKey);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                routingKey,
                event
        );
    }
}