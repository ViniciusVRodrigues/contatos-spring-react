package com.contatos.api.controller;

import com.contatos.api.dto.LoginRequest;
import com.contatos.api.dto.LoginResponse;
import com.contatos.api.dto.UsuarioRegistroRequest;
import com.contatos.api.dto.UsuarioResponse;
import com.contatos.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user in the system
     * @param request User registration data (name, email, password)
     * @return Created user information
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRegistroRequest request) {
        UsuarioResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns JWT token
     * @param request Login credentials (email and password)
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if an email is already registered in the system
     * @param email Email to verify
     * @return JSON with 'exists' boolean field
     */
    @GetMapping("/verificar-email")
    public ResponseEntity<Map<String, Boolean>> verificarEmail(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
