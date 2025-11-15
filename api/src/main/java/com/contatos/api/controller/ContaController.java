package com.contatos.api.controller;

import com.contatos.api.dto.DeletarContaRequest;
import com.contatos.api.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conta")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<Void> deletarConta(@Valid @RequestBody DeletarContaRequest request) {
        contaService.deletarConta(request);
        return ResponseEntity.noContent().build();
    }
}
