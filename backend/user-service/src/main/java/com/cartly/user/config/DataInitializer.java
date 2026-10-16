package com.cartly.user.config;

import com.cartly.user.entity.Address;
import com.cartly.user.entity.UserProfile;
import com.cartly.user.repository.AddressRepository;
import com.cartly.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) {
        if (!userProfileRepository.existsByUserId(1L)) {
            userProfileRepository.save(UserProfile.builder()
                    .userId(1L)
                    .name("Admin Cartly")
                    .email("admin@cartly.com")
                    .phone("+1 800-CARTLY-1")
                    .avatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150")
                    .build());
        }

        if (!userProfileRepository.existsByUserId(2L)) {
            userProfileRepository.save(UserProfile.builder()
                    .userId(2L)
                    .name("John Doe")
                    .email("customer@cartly.com")
                    .phone("+1 555-0199")
                    .avatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150")
                    .build());

            addressRepository.save(Address.builder()
                    .userId(2L)
                    .fullName("John Doe")
                    .phone("+1 555-0199")
                    .streetAddress("742 Evergreen Terrace")
                    .city("Springfield")
                    .state("OR")
                    .zipCode("97477")
                    .country("United States")
                    .isDefault(true)
                    .build());

            log.info("Initialized default user profiles and addresses");
        }
    }
}
