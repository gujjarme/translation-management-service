package com.digitaltolk.translation_service.feature.auth;

import com.digitaltolk.translation_service.dao.Translation;
import com.digitaltolk.translation_service.repository.TranslationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFeatureTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TranslationRepository translationRepository;

    @Test
    void shouldCompleteAuthFlowAndAccessProtectedEndpoint() throws Exception {
        // Register user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"featureUser\",\"password\":\"Pass123$\"}"))
                .andExpect(status().is(201));

        // Login and get token
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"featureUser\",\"password\":\"Pass123$\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String token = loginResult.getResponse().getContentAsString();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Create a translation (protected endpoint)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/translations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\": \"greeting\", \"locale\": \"en\", \"content\": \"Hello\", \"tags\": [\"common\"]}"))
                .andExpect(status().isOk());

        // Access translations export endpoint (protected)
        MvcResult exportResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/translations/export")
                        .param("locale", "en")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify export content
        String exportContent = exportResult.getResponse().getContentAsString();
        assertTrue(exportContent.contains("greeting"));
        assertTrue(exportContent.contains("Hello"));


    }
}