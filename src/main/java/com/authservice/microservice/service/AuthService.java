package com.authservice.microservice.service;

import com.authservice.microservice.dto.AuthenticationResponse;
import com.authservice.microservice.dto.LoginRequest;
import com.authservice.microservice.dto.RefreshTokenRequest;
import com.authservice.microservice.dto.RegisterRequest;
import com.authservice.microservice.entity.RefreshToken;
import com.authservice.microservice.entity.User;
import com.authservice.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

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
                .role(com.authservice.microservice.enums.Role.ROLE_USER) // Default role
                .build();
        userRepository.save(newUser);

        return "User registered successfully!";
    }

    public AuthenticationResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }

        // Generate both tokens
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo) // Extracts the User object linked to the token
                .map(user -> {
                    // Generate a fresh access token for this user
                    String accessToken = jwtService.generateToken(user);

                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.refreshToken()) // Keep using the same refresh token
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }


}