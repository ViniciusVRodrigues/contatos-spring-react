package com.contatos.api.controller;

import com.contatos.api.dto.ContatoRequest;
import com.contatos.api.dto.ContatoResponse;
import com.contatos.api.service.ContatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contatos")
@RequiredArgsConstructor
@Tag(name = "Contatos", description = "Endpoints para gerenciamento de contatos")
@SecurityRequirement(name = "bearerAuth")
public class ContatoController {

    private final ContatoService contatoService;

    @GetMapping
    @Operation(
        summary = "Listar contatos",
        description = "Lista todos os contatos do usuário autenticado com paginação e busca opcional por nome ou CPF"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contatos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<Page<ContatoResponse>> listContatos(
            @Parameter(description = "Buscar por nome ou CPF") 
            @RequestParam(required = false) String search,
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo e direção de ordenação (ex: nome,asc)")
            @RequestParam(defaultValue = "nome,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ContatoResponse> contatos = contatoService.listContatos(search, pageable);
        return ResponseEntity.ok(contatos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar contato por ID",
        description = "Retorna os dados completos de um contato específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato encontrado"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<ContatoResponse> getContato(
            @Parameter(description = "ID do contato") 
            @PathVariable Long id) {
        ContatoResponse contato = contatoService.getContato(id);
        return ResponseEntity.ok(contato);
    }

    @PostMapping
    @Operation(
        summary = "Criar novo contato",
        description = "Cadastra um novo contato com validação de CPF único e busca automática de coordenadas via Google Maps"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já cadastrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<ContatoResponse> createContato(@Valid @RequestBody ContatoRequest request) {
        ContatoResponse contato = contatoService.createContato(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contato);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar contato",
        description = "Atualiza os dados de um contato existente. Se o endereço for alterado, as coordenadas serão recalculadas automaticamente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<ContatoResponse> updateContato(
            @Parameter(description = "ID do contato")
            @PathVariable Long id,
            @Valid @RequestBody ContatoRequest request) {
        ContatoResponse contato = contatoService.updateContato(id, request);
        return ResponseEntity.ok(contato);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar contato",
        description = "Remove permanentemente um contato do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contato deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    public ResponseEntity<Void> deleteContato(
            @Parameter(description = "ID do contato")
            @PathVariable Long id) {
        contatoService.deleteContato(id);
        return ResponseEntity.noContent().build();
    }
}
