package com.authservice.microservice.repository;

import com.authservice.microservice.entity.User;
import com.authservice.microservice.entity.RefreshToken; // Adjust package name to wherever your RefreshToken class lives
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserInfo(User userInfo); // This will now correctly expect your custom User!
}