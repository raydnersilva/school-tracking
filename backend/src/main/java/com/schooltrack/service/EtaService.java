package com.schooltrack.service;

import com.schooltrack.dto.EtaResponse;
import com.schooltrack.model.Bus;
import com.schooltrack.model.RouteStop;
import com.schooltrack.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final BusRepository busRepository;

    private static final double AVG_SPEED_KMH = 30.0;

    public List<EtaResponse> calculateEta(Long busId, List<RouteStop> stops) {
        Bus bus = busRepository.findById(busId).orElse(null);
        if (bus == null || bus.getCurrentLatitude() == null || bus.getCurrentLongitude() == null) {
            return List.of();
        }

        List<EtaResponse> etas = new ArrayList<>();
        for (RouteStop stop : stops) {
            double distanceKm = calculateDistanceKm(
                    bus.getCurrentLatitude(), bus.getCurrentLongitude(),
                    stop.getLatitude(), stop.getLongitude()
            );
            int estimatedMinutes = (int) Math.ceil((distanceKm / AVG_SPEED_KMH) * 60);

            etas.add(new EtaResponse(
                    busId,
                    stop.getId(),
                    stop.getName(),
                    estimatedMinutes,
                    Math.round(distanceKm * 100.0) / 100.0
            ));
        }
        return etas;
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
