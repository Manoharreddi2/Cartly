package com.cartly.auth.config;

import com.cartly.auth.entity.User;
import com.cartly.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@cartly.com")) {
            User admin = User.builder()
                    .name("Admin Cartly")
                    .email("admin@cartly.com")
                    .phone("+1 800-CARTLY-1")
                    .password(passwordEncoder.encode("Admin@123"))
                    .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                    .build();
            userRepository.save(admin);
            log.info("Initialized default admin user: admin@cartly.com / Admin@123");
        }

        if (!userRepository.existsByEmail("customer@cartly.com")) {
            User customer = User.builder()
                    .name("John Doe")
                    .email("customer@cartly.com")
                    .phone("+1 555-0199")
                    .password(passwordEncoder.encode("Customer@123"))
                    .roles(Set.of("ROLE_USER"))
                    .build();
            userRepository.save(customer);
            log.info("Initialized default customer user: customer@cartly.com / Customer@123");
        }
    }
}
