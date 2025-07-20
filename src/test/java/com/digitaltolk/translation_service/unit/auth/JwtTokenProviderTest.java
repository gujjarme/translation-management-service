package com.digitaltolk.translation_service.unit.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.digitaltolk.translation_service.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtTokenProviderTest {


    private final String secret = "testSecret123456789012345678901212345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    private final long validity = 1000; // 1 second for testing
    private JwtTokenProvider provider = new JwtTokenProvider(secret,validity);


    @Test
    void shouldCreateAndValidateToken() {
        // Create token
        String token = provider.createToken("testUser");

        // Validate token
        assertTrue(provider.validateToken(token));
        assertEquals("testUser", provider.getUsername(token));
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        // Create token
        String token = provider.createToken("testUser");

        // Wait for expiration
        Thread.sleep(validity + 100);

        // Verify expiration
        assertFalse(provider.validateToken(token));
    }

    @Test
    void getAuthentication_shouldReturnPrincipal() {
        // Setup
        String token = provider.createToken("authUser");

        // Execute
        Authentication auth = provider.getAuthentication(token);

        // Verify
        assertEquals("authUser", auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertTrue(auth.getAuthorities().isEmpty());
    }
}