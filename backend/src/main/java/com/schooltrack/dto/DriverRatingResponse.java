package com.schooltrack.dto;

import java.time.LocalDateTime;

public record DriverRatingResponse(
        Long id,
        Long driverId,
        String driverName,
        Long parentId,
        String parentName,
        int rating,
        String comment,
        LocalDateTime createdAt
) {}
