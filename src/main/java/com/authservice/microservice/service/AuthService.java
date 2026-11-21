package com.authservice.microservice.service;

import com.authservice.microservice.dto.*;
import com.authservice.microservice.entity.PasswordResetToken;
import com.authservice.microservice.entity.RefreshToken;
import com.authservice.microservice.entity.User;
import com.authservice.microservice.enums.Role;
import com.authservice.microservice.event.NotificationEvent;
import com.authservice.microservice.publisher.NotificationEventPublisher;
import com.authservice.microservice.repository.PasswordResetTokenRepository;
import com.authservice.microservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NotificationEventPublisher notificationEventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already taken!");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(encodedPassword)
                .role(com.authservice.microservice.enums.Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);

        NotificationEvent event = NotificationEvent.builder()
                .eventType("USER_REGISTERED")
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .metadata(Map.of("userId", savedUser.getId())) // Pass userId via metadata for user-service
                .build();

        notificationEventPublisher.publishEvent("user.registered", event);

        return "User registered successfully!";
    }

    public AuthenticationResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        if (!user.isEnabled()) {
            // Throw a 403 Forbidden so the client knows it is an access restriction
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password!");
        }

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setRole(newRole);
        userRepository.save(user);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user);
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.refreshToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.email()));

        // Remove any existing token for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Generate new token (valid for 15 minutes)
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Publish event for notification-service
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token; // Adjust frontend URL later
        NotificationEvent event = NotificationEvent.builder()
                .eventType("PASSWORD_RESET_REQUESTED")
                .email(user.getEmail())
                .username(user.getUsername())
                .metadata(Map.of("resetLink", resetLink))
                .build();

        notificationEventPublisher.publishEvent("user.password.reset", event);

        return "Password reset link has been sent to your email.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new RuntimeException("Invalid password reset token!"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Password reset token has expired!");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Clean up token
        passwordResetTokenRepository.delete(resetToken);

        // Publish success event
        NotificationEvent event = NotificationEvent.builder()
                .eventType("PASSWORD_CHANGED")
                .email(user.getEmail())
                .username(user.getUsername())
                .metadata(Map.of("userId", user.getId()))
                .build();

        notificationEventPublisher.publishEvent("user.password.changed", event);

        return "Password has been successfully updated.";
    }

    @Transactional
    public String changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            // Throwing a 400 Bad Request if the old password doesn't match
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Incorrect current password."
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Publish success event for the notification-service
        NotificationEvent event = NotificationEvent.builder()
                .eventType("PASSWORD_CHANGED")
                .email(user.getEmail())
                .username(user.getUsername())
                .metadata(java.util.Map.of("userId", user.getId()))
                .build();

        notificationEventPublisher.publishEvent("user.password.changed", event);

        return "Password successfully updated.";
    }
}