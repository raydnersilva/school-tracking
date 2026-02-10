package com.schooltrack.repository;

import com.schooltrack.model.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {
    List<BusRoute> findByActiveTrue();
    List<BusRoute> findByBusId(Long busId);
}
