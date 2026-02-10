package com.schooltrack.dto;

public record DashboardStatsResponse(
        long totalStudents,
        long totalBuses,
        long activeBuses,
        long totalRoutes,
        long activeRoutes,
        long totalDrivers,
        long tripsToday,
        long tripsCompleted,
        Double averageDriverRating
) {}
