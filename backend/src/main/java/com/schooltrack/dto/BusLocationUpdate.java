package com.schooltrack.dto;

public record BusLocationUpdate(
        Long busId,
        Double latitude,
        Double longitude,
        long timestamp
) {}
