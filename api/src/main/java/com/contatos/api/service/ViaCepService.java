package com.contatos.api.service;

import com.contatos.api.dto.ViaCepResponse;
import com.contatos.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private static final String VIACEP_BASE_URL = "https://viacep.com.br/ws";
    private final RestClient restClient = RestClient.create();

    public ViaCepResponse buscarPorCep(String cep) {
        try {
            ViaCepResponse response = restClient.get()
                    .uri(VIACEP_BASE_URL + "/" + cep + "/json/")
                    .retrieve()
                    .body(ViaCepResponse.class);

            if (response != null && response.getCep() != null) {
                return response;
            }
            throw new ResourceNotFoundException("CEP não encontrado");
        } catch (RestClientException e) {
            throw new ResourceNotFoundException("CEP não encontrado");
        }
    }

    public List<ViaCepResponse> buscarEnderecos(String uf, String cidade, String logradouro) {
        try {
            ViaCepResponse[] response = restClient.get()
                    .uri(VIACEP_BASE_URL + "/" + uf + "/" + cidade + "/" + logradouro + "/json/")
                    .retrieve()
                    .body(ViaCepResponse[].class);

            if (response != null && response.length > 0) {
                return List.of(response);
            }
            return List.of();
        } catch (RestClientException e) {
            return List.of();
        }
    }
}
