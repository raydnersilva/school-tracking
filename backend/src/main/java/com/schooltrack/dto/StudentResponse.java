package com.schooltrack.dto;

public record StudentResponse(
        Long id,
        String name,
        String grade,
        String school,
        String parentName,
        Long busRouteId,
        String busRouteName
) {}
