package com.cartly.auth.controller;

import com.cartly.auth.dto.*;
import com.cartly.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validate(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.ok(isValid ? "Token is valid" : "Token is invalid", isValid));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        UserResponse user = authService.getCurrentUser(authHeader);
        return ResponseEntity.ok(ApiResponse.ok("User profile fetched successfully", user));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PasswordUpdateRequest request) {
        authService.updatePassword(authHeader, request);
        return ResponseEntity.ok(ApiResponse.ok("Password updated successfully", null));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = authService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User details fetched", user));
    }
}
