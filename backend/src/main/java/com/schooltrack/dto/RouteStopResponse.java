package com.schooltrack.dto;

public record RouteStopResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        int stopOrder,
        String estimatedTime
) {}
