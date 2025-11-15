package com.contatos.api.service;

import com.contatos.api.dto.LoginRequest;
import com.contatos.api.dto.LoginResponse;
import com.contatos.api.dto.UsuarioRegistroRequest;
import com.contatos.api.dto.UsuarioResponse;
import com.contatos.api.exception.BusinessException;
import com.contatos.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void shouldRegisterNewUser() {
        UsuarioRegistroRequest request = UsuarioRegistroRequest.builder()
                .nome("Test User")
                .email("test@example.com")
                .senha("password123")
                .build();

        UsuarioResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Test User", response.getNome());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(usuarioRepository.existsByEmail("test@example.com"));
    }

    @Test
    void shouldNotRegisterDuplicateEmail() {
        UsuarioRegistroRequest request = UsuarioRegistroRequest.builder()
                .nome("Test User")
                .email("duplicate@example.com")
                .senha("password123")
                .build();

        authService.register(request);

        assertThrows(BusinessException.class, () -> authService.register(request));
    }

    @Test
    void shouldLoginSuccessfully() {
        UsuarioRegistroRequest registerRequest = UsuarioRegistroRequest.builder()
                .nome("Login Test")
                .email("login@example.com")
                .senha("password123")
                .build();
        authService.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("login@example.com")
                .senha("password123")
                .build();

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertNotNull(response.getUsuario());
        assertEquals("Login Test", response.getUsuario().getNome());
    }

    @Test
    void shouldNotLoginWithWrongPassword() {
        UsuarioRegistroRequest registerRequest = UsuarioRegistroRequest.builder()
                .nome("Wrong Password Test")
                .email("wrong@example.com")
                .senha("correctpassword")
                .build();
        authService.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("wrong@example.com")
                .senha("wrongpassword")
                .build();

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
