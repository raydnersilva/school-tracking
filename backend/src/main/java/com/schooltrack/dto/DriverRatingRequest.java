package com.schooltrack.dto;

public record DriverRatingRequest(
        Long driverId,
        Long tripId,
        int rating,
        String comment
) {}
