package com.schooltrack.dto;

import java.time.LocalDateTime;

public record GeofenceAlertResponse(
        Long busId,
        String busLicensePlate,
        Long zoneId,
        String zoneName,
        String eventType,
        LocalDateTime timestamp
) {}
