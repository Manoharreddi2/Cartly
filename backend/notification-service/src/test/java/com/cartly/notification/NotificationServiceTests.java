package com.cartly.notification;

import com.cartly.notification.dto.NotificationRequest;
import com.cartly.notification.dto.NotificationResponse;
import com.cartly.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NotificationServiceTests {

    @Autowired
    private NotificationService notificationService;

    @Test
    void testSendAndRetrieveNotifications() {
        Long userId = 2L;

        NotificationRequest req = NotificationRequest.builder()
                .userId(userId)
                .title("Order Confirmed")
                .message("Your order #1001 has been confirmed and is being prepared.")
                .type("ORDER")
                .build();

        NotificationResponse sent = notificationService.sendNotification(req);
        assertNotNull(sent.getId());
        assertFalse(sent.isRead());

        List<NotificationResponse> list = notificationService.getUserNotifications(userId);
        assertTrue(list.size() >= 1);

        NotificationResponse updated = notificationService.markAsRead(sent.getId(), userId);
        assertTrue(updated.isRead());
    }
}
