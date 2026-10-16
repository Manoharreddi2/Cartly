package com.cartly.notification.config;

import com.cartly.notification.entity.NotificationRecord;
import com.cartly.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        if (notificationRepository.count() == 0) {
            NotificationRecord welcome = NotificationRecord.builder()
                    .userId(2L)
                    .title("Welcome to Cartly!")
                    .message("Enjoy 10% off your first tech or fashion order. Everything you need, just a cart away!")
                    .type("PROMO")
                    .isRead(false)
                    .build();

            notificationRepository.save(welcome);
            log.info("Initialized default welcome notification");
        }
    }
}
