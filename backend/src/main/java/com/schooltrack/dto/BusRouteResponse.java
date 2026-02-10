package com.schooltrack.dto;

import java.util.List;

public record BusRouteResponse(
        Long id,
        String name,
        String description,
        Long busId,
        String busLicensePlate,
        boolean active,
        List<RouteStopResponse> stops
) {}
