package com.contatos.api.controller;

import com.contatos.api.dto.LoginRequest;
import com.contatos.api.dto.LoginResponse;
import com.contatos.api.dto.UsuarioRegistroRequest;
import com.contatos.api.dto.UsuarioResponse;
import com.contatos.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro, login e verificação de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário no sistema com validação de email único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRegistroRequest request) {
        UsuarioResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Fazer login",
        description = "Autentica o usuário e retorna um token JWT válido por 24 horas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-email")
    @Operation(
        summary = "Verificar disponibilidade de email",
        description = "Verifica se um email já está cadastrado no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    public ResponseEntity<Map<String, Boolean>> verificarEmail(
            @Parameter(description = "Email a ser verificado")
            @RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
