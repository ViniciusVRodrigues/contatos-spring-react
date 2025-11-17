package com.contatos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleGeocodingResponse {
    
    private List<Result> results;
    private String status;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Geometry geometry;
        private String formatted_address;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        private Location location;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double lat;
        private Double lng;
    }
}
