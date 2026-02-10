package com.schooltrack.repository;

import com.schooltrack.model.GeofenceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GeofenceZoneRepository extends JpaRepository<GeofenceZone, Long> {
    List<GeofenceZone> findByActiveTrue();
    List<GeofenceZone> findByBusRouteIdAndActiveTrue(Long busRouteId);
}
