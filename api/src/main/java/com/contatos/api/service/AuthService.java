package com.contatos.api.service;

import com.contatos.api.dto.*;
import com.contatos.api.exception.BusinessException;
import com.contatos.api.model.Usuario;
import com.contatos.api.repository.UsuarioRepository;
import com.contatos.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations including user registration, login, and email verification
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system
     * @param request Registration data containing name, email, and password
     * @return User information without sensitive data
     * @throws BusinessException if email is already registered
     */
    @Transactional
    public UsuarioResponse register(UsuarioRegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .build();

        usuario = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }

    /**
     * Authenticates a user and generates a JWT token
     * @param request Login credentials (email and password)
     * @return JWT token and user information
     * @throws BusinessException if user not found or credentials are invalid
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(usuario.getEmail());

        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioResponse)
                .build();
    }

    /**
     * Checks if an email is already registered in the system
     * @param email Email address to verify
     * @return true if email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
