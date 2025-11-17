package com.contatos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criar ou atualizar um contato")
public class ContatoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do contato", example = "João da Silva")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Schema(description = "CPF do contato (apenas números)", example = "12345678900")
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone do contato com DDD", example = "11987654321")
    private String telefone;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Schema(description = "CEP do endereço (apenas números)", example = "01310100")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Nome da rua/avenida", example = "Avenida Paulista")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Schema(description = "Número do endereço", example = "1578")
    private String numero;

    @Schema(description = "Complemento do endereço (opcional)", example = "Apto 101")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Schema(description = "Bairro do endereço", example = "Bela Vista")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Schema(description = "Sigla do estado (UF)", example = "SP")
    private String estado;

    @Schema(description = "Latitude (preenchida automaticamente se não fornecida)", example = "-23.561414")
    private Double latitude;

    @Schema(description = "Longitude (preenchida automaticamente se não fornecida)", example = "-46.656071")
    private Double longitude;
}
