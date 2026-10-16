package com.cartly.user;

import com.cartly.user.dto.AddressRequest;
import com.cartly.user.dto.AddressResponse;
import com.cartly.user.dto.ProfileRequest;
import com.cartly.user.entity.UserProfile;
import com.cartly.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void testGetProfile() {
        UserProfile profile = userService.getProfile(2L);
        assertNotNull(profile);
        assertEquals("John Doe", profile.getName());
    }

    @Test
    void testUpdateProfile() {
        ProfileRequest request = ProfileRequest.builder()
                .name("John Updated")
                .phone("+1 555-9999")
                .avatarUrl("https://example.com/avatar.jpg")
                .build();

        UserProfile updated = userService.updateProfile(2L, request);
        assertEquals("John Updated", updated.getName());
        assertEquals("+1 555-9999", updated.getPhone());
    }

    @Test
    void testAddAndGetAddresses() {
        AddressRequest request = AddressRequest.builder()
                .fullName("Jane Doe")
                .phone("+1 555-4321")
                .streetAddress("123 Main St")
                .city("Seattle")
                .state("WA")
                .zipCode("98101")
                .country("United States")
                .isDefault(false)
                .build();

        AddressResponse created = userService.addAddress(2L, request);
        assertNotNull(created.getId());
        assertEquals("Seattle", created.getCity());

        List<AddressResponse> list = userService.getAddresses(2L);
        assertTrue(list.size() >= 2);
    }
}
