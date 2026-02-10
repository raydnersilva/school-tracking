package com.schooltrack.dto;

public record ScheduleResponse(
        Long id,
        String period,
        String startTime,
        String endTime,
        String busPickupTime,
        String busDropoffTime,
        Long busRouteId,
        String busRouteName,
        Long studentId,
        String studentName
) {}
