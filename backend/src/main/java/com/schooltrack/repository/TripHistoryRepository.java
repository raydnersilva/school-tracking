package com.schooltrack.repository;

import com.schooltrack.model.TripHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripHistoryRepository extends JpaRepository<TripHistory, Long> {
    List<TripHistory> findByBusRouteIdOrderByStartedAtDesc(Long busRouteId);
    List<TripHistory> findByDriverIdOrderByStartedAtDesc(Long driverId);
    Optional<TripHistory> findByDriverIdAndStatus(Long driverId, TripHistory.TripStatus status);
    List<TripHistory> findByStartedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(TripHistory.TripStatus status);
}
