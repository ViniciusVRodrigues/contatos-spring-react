package com.contatos.api.service;

import com.contatos.api.dto.GoogleGeocodingResponse;
import com.contatos.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${google.maps.api.key:}")
    private String apiKey;

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private final RestClient restClient = RestClient.create();

    public GoogleGeocodingResponse.Location getCoordinates(String address) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException("Google Maps API key não configurada");
        }

        try {
            GoogleGeocodingResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .path("/maps/api/geocode/json")
                            .queryParam("address", address)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(GoogleGeocodingResponse.class);

            if (response != null && "OK".equals(response.getStatus()) 
                    && response.getResults() != null && !response.getResults().isEmpty()) {
                return response.getResults().get(0).getGeometry().getLocation();
            }
            
            throw new BusinessException("Não foi possível obter coordenadas para o endereço fornecido");
        } catch (RestClientException e) {
            throw new BusinessException("Erro ao buscar coordenadas: " + e.getMessage());
        }
    }

    public GoogleGeocodingResponse.Location getCoordinates(String logradouro, String numero, 
                                                           String bairro, String cidade, String estado, String cep) {
        String fullAddress = String.format("%s, %s - %s, %s - %s, Brasil, %s",
                logradouro, numero, bairro, cidade, estado, cep);
        return getCoordinates(fullAddress);
    }
}
