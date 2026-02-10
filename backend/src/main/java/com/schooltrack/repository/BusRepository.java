package com.schooltrack.repository;

import com.schooltrack.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByLicensePlate(String licensePlate);
    List<Bus> findByActiveTrue();
    List<Bus> findByDriverId(Long driverId);
}
