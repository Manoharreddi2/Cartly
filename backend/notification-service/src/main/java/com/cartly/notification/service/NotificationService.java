package com.cartly.notification.service;

import com.cartly.notification.dto.NotificationRequest;
import com.cartly.notification.dto.NotificationResponse;
import com.cartly.notification.entity.NotificationRecord;
import com.cartly.notification.exception.ResourceNotFoundException;
import com.cartly.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        NotificationRecord record = NotificationRecord.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType() != null ? request.getType() : "ORDER")
                .isRead(false)
                .build();

        NotificationRecord saved = notificationRepository.save(record);
        log.info("Sent notification to user {}: [{}] {}", request.getUserId(), saved.getType(), saved.getTitle());
        return toResponse(saved);
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(Long id, Long userId) {
        NotificationRecord record = notificationRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        record.setRead(true);
        return toResponse(notificationRepository.save(record));
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationResponse toResponse(NotificationRecord r) {
        return NotificationResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .title(r.getTitle())
                .message(r.getMessage())
                .type(r.getType())
                .isRead(r.isRead())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
