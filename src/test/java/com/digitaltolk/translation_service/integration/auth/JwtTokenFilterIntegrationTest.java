package com.digitaltolk.translation_service.integration.auth;

import com.digitaltolk.translation_service.auth.JwtTokenFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.digitaltolk.translation_service.auth.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@AutoConfigureMockMvc
@SpringBootTest
//@WebMvcTest(JwtTokenFilterIntegrationTest.TestController.class)
@Import({JwtTokenFilter.class, JwtTokenFilterIntegrationTest.TestController.class})
class JwtTokenFilterIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private JwtTokenProvider tokenProvider;

    // Test endpoint
    //@Import(JwtTokenFilter.class)
    @RestController
    static class TestController {
        @GetMapping("/test")
        public String test() { return "ok"; }
    }

    @Test
    void shouldAuthenticateWithValidToken() throws Exception {
        // Setup
        when(tokenProvider.validateToken("valid.token")).thenReturn(true);
        when(tokenProvider.getUsername("valid.token")).thenReturn("user");

        // Execute & Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Bearer valid.token"))
                .andExpect(status().isOk());

        // Security context verification
       // assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldBlockInvalidTokens() throws Exception {
        // Setup
        when(tokenProvider.validateToken("invalid.token")).thenReturn(false);

        // Execute & Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }
}