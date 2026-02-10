package com.schooltrack.dto;

import java.time.LocalDateTime;

public record TripHistoryResponse(
        Long id,
        Long busRouteId,
        String busRouteName,
        Long driverId,
        String driverName,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String status,
        int studentsBoarded,
        int studentsDropped,
        Double distanceKm,
        Integer durationMinutes
) {}
