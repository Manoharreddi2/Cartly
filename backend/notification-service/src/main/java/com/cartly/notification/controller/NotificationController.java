package com.cartly.notification.controller;

import com.cartly.notification.dto.ApiResponse;
import com.cartly.notification.dto.NotificationRequest;
import com.cartly.notification.dto.NotificationResponse;
import com.cartly.notification.service.NotificationService;
import com.cartly.notification.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtils jwtUtils;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponse>> send(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Notification sent", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        List<NotificationResponse> list = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved", list));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        NotificationResponse response = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", response));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.ok("Unread count retrieved", count));
    }
}
