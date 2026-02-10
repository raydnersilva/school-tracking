package com.schooltrack.controller;

import com.schooltrack.dto.TripHistoryResponse;
import com.schooltrack.repository.TripHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripHistoryController {

    private final TripHistoryRepository tripHistoryRepository;

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TripHistoryResponse>> getTripsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(
                tripHistoryRepository.findByBusRouteIdOrderByStartedAtDesc(routeId).stream()
                        .map(t -> new TripHistoryResponse(
                                t.getId(),
                                t.getBusRoute().getId(),
                                t.getBusRoute().getName(),
                                t.getDriver().getId(),
                                t.getDriver().getName(),
                                t.getStartedAt(),
                                t.getEndedAt(),
                                t.getStatus().name(),
                                t.getStudentsBoarded(),
                                t.getStudentsDropped(),
                                t.getDistanceKm(),
                                t.getDurationMinutes()
                        )).toList()
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<TripHistoryResponse>> getTripsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(List.of());
    }
}
