package com.contatos.api.controller;

import com.contatos.api.dto.ViaCepResponse;
import com.contatos.api.service.ViaCepService;
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
public class EnderecoController {

    private final ViaCepService viaCepService;

    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponse> buscarPorCep(@PathVariable String cep) {
        ViaCepResponse response = viaCepService.buscarPorCep(cep);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ViaCepResponse>> buscarEnderecos(
            @RequestParam @Size(min = 2, max = 2) String uf,
            @RequestParam String cidade,
            @RequestParam @Size(min = 3) String logradouro) {
        List<ViaCepResponse> response = viaCepService.buscarEnderecos(uf, cidade, logradouro);
        return ResponseEntity.ok(response);
    }
}
