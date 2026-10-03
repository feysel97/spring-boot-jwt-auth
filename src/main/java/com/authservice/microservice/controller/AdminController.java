package com.authservice.microservice.controller;

import com.authservice.microservice.dto.UpdateRoleRequest;
import com.authservice.microservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Restricts access to ADMINs only
    public ResponseEntity<String> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        authService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok("User role updated successfully to " + request.getRole());
    }
}