/**
 * Plantilla para tests de Controller (JUnit 5 + MockMvc).
 * Copiar y adaptar al feature específico.
 */
package com.nomina.SERVICIO.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeatureService service;

    @Test
    void should_return201_when_validRequest() throws Exception {
        // GIVEN
        FeatureResponse response = new FeatureResponse(/* datos esperados */);
        when(service.create(any())).thenReturn(response);

        String requestBody = objectMapper.writeValueAsString(
            new FeatureRequest(/* datos válidos */)
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void should_return400_when_invalidRequest() throws Exception {
        // GIVEN
        String invalidBody = "{}"; // campos obligatorios faltantes

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return404_when_notFound() throws Exception {
        // GIVEN
        when(service.findById(999L))
            .thenThrow(new FeatureNotFoundException("No encontrado"));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/features/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_return200_when_listAll() throws Exception {
        // GIVEN
        when(service.findAll()).thenReturn(java.util.List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/features"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
