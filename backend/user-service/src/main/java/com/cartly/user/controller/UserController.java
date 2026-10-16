package com.cartly.user.controller;

import com.cartly.user.dto.AddressRequest;
import com.cartly.user.dto.AddressResponse;
import com.cartly.user.dto.ApiResponse;
import com.cartly.user.dto.ProfileRequest;
import com.cartly.user.entity.UserProfile;
import com.cartly.user.service.UserService;
import com.cartly.user.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> getProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        UserProfile profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ProfileRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        UserProfile updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        List<AddressResponse> addresses = userService.getAddresses(userId);
        return ResponseEntity.ok(ApiResponse.ok("Addresses retrieved", addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AddressRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        AddressResponse created = userService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Address added successfully", created));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        AddressResponse updated = userService.updateAddress(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Address updated successfully", updated));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        userService.deleteAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAllUsers() {
        List<UserProfile> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("All users retrieved", users));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> getUserById(@PathVariable Long userId) {
        UserProfile profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok("User retrieved", profile));
    }
}
