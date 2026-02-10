package com.schooltrack.dto;

import java.time.LocalDateTime;

public record BoardingRecordResponse(
        Long id,
        Long tripId,
        Long studentId,
        String studentName,
        String type,
        LocalDateTime timestamp,
        Double latitude,
        Double longitude
) {}
