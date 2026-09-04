package com.authservice.microservice.controller;

import com.authservice.microservice.dto.AuthenticationResponse;
import com.authservice.microservice.dto.LoginRequest;
import com.authservice.microservice.dto.RefreshTokenRequest;
import com.authservice.microservice.dto.RegisterRequest;
import com.authservice.microservice.entity.RefreshToken;
import com.authservice.microservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
