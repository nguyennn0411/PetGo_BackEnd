package com.example.petgo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class RoutingService {

    private static final double EARTH_RADIUS_KM = 6371;
    private final WebClient webClient;

    public RoutingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://router.project-osrm.org")
                .build();
    }

    public double getDrivingDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = webClient.get()
                    .uri("/route/v1/driving/{lng1},{lat1};{lng2},{lat2}?overview=false",
                            lon1, lat1, lon2, lat2)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (body != null && "Ok".equals(body.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) body.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    Number distanceMeters = (Number) routes.get(0).get("distance");
                    if (distanceMeters != null) {
                        return distanceMeters.doubleValue() / 1000.0;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return haversineKm(lat1, lon1, lat2, lon2);
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * EARTH_RADIUS_KM;
    }
}
