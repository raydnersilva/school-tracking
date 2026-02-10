package com.schooltrack.dto;

public record BusResponse(
        Long id,
        String licensePlate,
        int capacity,
        String model,
        String driverName,
        Double currentLatitude,
        Double currentLongitude,
        boolean active
) {}
