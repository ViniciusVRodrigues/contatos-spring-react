package com.contatos.api.config;

import com.contatos.api.dto.GoogleGeocodingResponse;
import com.contatos.api.service.GoogleMapsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test configuration that provides mock beans for external services
 * This prevents tests from making real API calls to Google Maps, ViaCEP, etc.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mocks GoogleMapsService to avoid real API calls during tests
     * Returns mock coordinates for any address
     */
    @Bean
    @Primary
    public GoogleMapsService googleMapsService() {
        GoogleMapsService mockService = mock(GoogleMapsService.class);
        
        // Mock response for coordinate lookup
        GoogleGeocodingResponse.Location mockLocation = new GoogleGeocodingResponse.Location();
        mockLocation.setLat(-25.4284); // Curitiba coordinates
        mockLocation.setLng(-49.2733);
        
        try {
            // Mock for full address lookup
            when(mockService.getCoordinates(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
            )).thenReturn(mockLocation);
            
            // Mock for simple address string lookup
            when(mockService.getCoordinates(anyString())).thenReturn(mockLocation);
        } catch (Exception e) {
            // Should not happen in mock
        }
        
        return mockService;
    }
}
