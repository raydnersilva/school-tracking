package com.schooltrack.dto;

public record EtaResponse(
        Long busId,
        Long stopId,
        String stopName,
        int estimatedMinutes,
        Double distanceKm
) {}
