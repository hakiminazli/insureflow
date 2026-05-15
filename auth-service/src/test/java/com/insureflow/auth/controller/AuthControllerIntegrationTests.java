package com.insureflow.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insureflow.auth.dto.AuthResponse;
import com.insureflow.auth.dto.LoginRequest;
import com.insureflow.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginValidateAndReadCurrentUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("provider1");
        registerRequest.setEmail("provider1@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("provider1"))
                .andExpect(jsonPath("$.email").value("provider1@example.com"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("provider1");
        loginRequest.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readValue(loginResponse, AuthResponse.class).getToken();

        mockMvc.perform(get("/api/auth/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("provider1"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("MEMBER")));

        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("provider1"))
                .andExpect(jsonPath("$.email").value("provider1@example.com"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("MEMBER")));
    }

    @Test
    void validateReturnsFalseWhenTokenIsMissingOrInvalid() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));

        mockMvc.perform(get("/api/auth/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}
