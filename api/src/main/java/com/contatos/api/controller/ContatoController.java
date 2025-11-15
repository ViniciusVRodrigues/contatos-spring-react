package com.contatos.api.controller;

import com.contatos.api.dto.ContatoRequest;
import com.contatos.api.dto.ContatoResponse;
import com.contatos.api.service.ContatoService;
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
public class ContatoController {

    private final ContatoService contatoService;

    @GetMapping
    public ResponseEntity<Page<ContatoResponse>> listContatos(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
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
    public ResponseEntity<ContatoResponse> getContato(@PathVariable Long id) {
        ContatoResponse contato = contatoService.getContato(id);
        return ResponseEntity.ok(contato);
    }

    @PostMapping
    public ResponseEntity<ContatoResponse> createContato(@Valid @RequestBody ContatoRequest request) {
        ContatoResponse contato = contatoService.createContato(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContatoResponse> updateContato(
            @PathVariable Long id,
            @Valid @RequestBody ContatoRequest request) {
        ContatoResponse contato = contatoService.updateContato(id, request);
        return ResponseEntity.ok(contato);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable Long id) {
        contatoService.deleteContato(id);
        return ResponseEntity.noContent().build();
    }
}
