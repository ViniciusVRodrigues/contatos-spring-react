package com.contatos.api.controller;

import com.contatos.api.dto.ViaCepResponse;
import com.contatos.api.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Endereços", description = "Endpoints para consulta de CEP e busca de endereços via ViaCEP")
@SecurityRequirement(name = "bearerAuth")
public class EnderecoController {

    private final ViaCepService viaCepService;

    @GetMapping("/cep/{cep}")
    @Operation(
        summary = "Buscar endereço por CEP",
        description = "Consulta os dados completos de um endereço através do CEP usando a API ViaCEP"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<ViaCepResponse> buscarPorCep(
            @Parameter(description = "CEP com 8 dígitos (apenas números)")
            @PathVariable String cep) {
        ViaCepResponse response = viaCepService.buscarPorCep(cep);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Buscar endereços por logradouro",
        description = "Busca endereços por UF, cidade e nome do logradouro (mínimo 3 caracteres)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de endereços encontrados"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<List<ViaCepResponse>> buscarEnderecos(
            @Parameter(description = "Sigla do estado (UF) com 2 caracteres - ex: SP, RJ")
            @RequestParam @Size(min = 2, max = 2) String uf,
            @Parameter(description = "Nome da cidade")
            @RequestParam String cidade,
            @Parameter(description = "Nome do logradouro (mínimo 3 caracteres)")
            @RequestParam @Size(min = 3) String logradouro) {
        List<ViaCepResponse> response = viaCepService.buscarEnderecos(uf, cidade, logradouro);
        return ResponseEntity.ok(response);
    }
}
