package com.schooltrack.service;

import com.schooltrack.dto.GeofenceAlertResponse;
import com.schooltrack.model.Bus;
import com.schooltrack.model.GeofenceZone;
import com.schooltrack.repository.GeofenceZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeofenceService {

    private final GeofenceZoneRepository geofenceZoneRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void checkGeofence(Bus bus) {
        if (bus.getCurrentLatitude() == null || bus.getCurrentLongitude() == null) return;

        List<GeofenceZone> zones = geofenceZoneRepository.findByActiveTrue();
        for (GeofenceZone zone : zones) {
            double distance = calculateDistance(
                    bus.getCurrentLatitude(), bus.getCurrentLongitude(),
                    zone.getLatitude(), zone.getLongitude()
            );

            String eventType = distance <= zone.getRadiusMeters() ? "ENTERED" : "EXITED";

            if (distance <= zone.getRadiusMeters() * 1.1) {
                GeofenceAlertResponse alert = new GeofenceAlertResponse(
                        bus.getId(),
                        bus.getLicensePlate(),
                        zone.getId(),
                        zone.getName(),
                        eventType,
                        LocalDateTime.now()
                );
                messagingTemplate.convertAndSend("/topic/geofence/alerts", alert);
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
