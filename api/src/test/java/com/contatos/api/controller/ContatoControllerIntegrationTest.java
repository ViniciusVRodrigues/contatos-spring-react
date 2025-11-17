package com.contatos.api.controller;

import com.contatos.api.config.TestConfig;
import com.contatos.api.dto.ContatoRequest;
import com.contatos.api.dto.LoginRequest;
import com.contatos.api.dto.LoginResponse;
import com.contatos.api.dto.UsuarioRegistroRequest;
import com.contatos.api.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Contact API endpoints
 * Tests HTTP layer, validation, authentication, and authorization
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestConfig.class)
class ContatoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Create and authenticate user for tests
        String email = "integration-test@example.com";
        UsuarioRegistroRequest registerRequest = UsuarioRegistroRequest.builder()
                .nome("Integration Test User")
                .email(email)
                .senha("password123")
                .build();

        authService.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .senha("password123")
                .build();

        LoginResponse loginResponse = authService.login(loginRequest);
        jwtToken = loginResponse.getToken();
    }

    /**
     * Test: Should require authentication for listing contacts
     * Note: Spring Security 6 returns 403 (Forbidden) instead of 401 (Unauthorized) 
     * when no authentication is provided
     */
    @Test
    void shouldRequireAuthenticationForListContatos() throws Exception {
        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: Should list contacts with valid JWT token
     */
    @Test
    void shouldListContatosWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/contatos")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    /**
     * Test: Should create contact with valid data
     * Business Rule: Validates required fields and CPF format
     */
    @Test
    void shouldCreateContatoWithValidData() throws Exception {
        ContatoRequest request = ContatoRequest.builder()
                .nome("API Test Contact")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua API")
                .numero("123")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        mockMvc.perform(post("/api/contatos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("API Test Contact"))
                .andExpect(jsonPath("$.cpf").value("12345678909"));
    }

    /**
     * Test: Should reject contact with invalid CPF
     */
    @Test
    void shouldRejectInvalidCpf() throws Exception {
        ContatoRequest request = ContatoRequest.builder()
                .nome("Invalid CPF Test")
                .cpf("12345678901") // Invalid CPF
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        mockMvc.perform(post("/api/contatos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF inválido"));
    }

    /**
     * Test: Should support pagination parameters
     */
    @Test
    void shouldSupportPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/contatos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    /**
     * Test: Should support search parameter
     */
    @Test
    void shouldSupportSearchParameter() throws Exception {
        // First create a contact
        ContatoRequest request = ContatoRequest.builder()
                .nome("Searchable Contact")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua Busca")
                .numero("100")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        mockMvc.perform(post("/api/contatos")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then search for it
        mockMvc.perform(get("/api/contatos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("search", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
