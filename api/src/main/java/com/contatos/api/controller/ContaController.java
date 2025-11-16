package com.contatos.api.controller;

import com.contatos.api.dto.DeletarContaRequest;
import com.contatos.api.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conta")
@RequiredArgsConstructor
@Tag(name = "Conta", description = "Endpoints para gerenciamento da conta do usuário")
@SecurityRequirement(name = "bearerAuth")
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    @Operation(
        summary = "Deletar conta do usuário",
        description = "Remove permanentemente a conta do usuário autenticado e todos os seus contatos. Requer confirmação de senha."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha incorreta", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<Void> deletarConta(@Valid @RequestBody DeletarContaRequest request) {
        contaService.deletarConta(request);
        return ResponseEntity.noContent().build();
    }
}
