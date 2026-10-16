package com.cartly.auth;

import com.cartly.auth.dto.AuthResponse;
import com.cartly.auth.dto.LoginRequest;
import com.cartly.auth.dto.RegisterRequest;
import com.cartly.auth.exception.BadRequestException;
import com.cartly.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alice Wonder")
                .email("alice@example.com")
                .phone("+1 555-0101")
                .password("Password@123")
                .build();

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("alice@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    @Test
    void testRegisterDuplicateEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alice Wonder")
                .email("alice_dup@example.com")
                .phone("+1 555-0101")
                .password("Password@123")
                .build();

        authService.register(request);

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void testLoginSuccess() {
        // Pre-seeded customer user: customer@cartly.com / Customer@123
        LoginRequest request = LoginRequest.builder()
                .email("customer@cartly.com")
                .password("Customer@123")
                .build();

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("customer@cartly.com", response.getEmail());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("customer@cartly.com")
                .password("WrongPassword")
                .build();

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
