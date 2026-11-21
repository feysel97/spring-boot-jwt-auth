package com.authservice.microservice.listener;

import com.authservice.microservice.entity.User;
import com.authservice.microservice.repository.UserRepository;
import com.authservice.microservice.event.NotificationEvent; // Adjust import to your DTO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventListener {

    private final UserRepository userRepository;

    // IMPORTANT: auth-service needs its OWN unique queue name!
    @RabbitListener(queues = "auth.account.deactivated.queue")
    @Transactional
    public void handleAccountDeactivation(NotificationEvent event) {
        if ("ACCOUNT_DEACTIVATED".equals(event.getEventType())) {
            log.info("🔒 Received deactivation event for: {}", event.getEmail());

            userRepository.findByEmail(event.getEmail()).ifPresent(user -> {
                user.setEnabled(false);
                userRepository.save(user);
                log.info("✅ Auth-service successfully disabled user: {}", event.getEmail());
            });
        }
    }
}
