package com.schooltrack.service;

import com.schooltrack.dto.*;
import com.schooltrack.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoMapper {

    public BusResponse toBusResponse(Bus bus) {
        return new BusResponse(
                bus.getId(),
                bus.getLicensePlate(),
                bus.getCapacity(),
                bus.getModel(),
                bus.getDriver() != null ? bus.getDriver().getName() : null,
                bus.getCurrentLatitude(),
                bus.getCurrentLongitude(),
                bus.isActive()
        );
    }

    public StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getGrade(),
                student.getSchool(),
                student.getParent() != null ? student.getParent().getName() : null,
                student.getBusRoute() != null ? student.getBusRoute().getId() : null,
                student.getBusRoute() != null ? student.getBusRoute().getName() : null
        );
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    public RouteStopResponse toRouteStopResponse(RouteStop stop) {
        return new RouteStopResponse(
                stop.getId(),
                stop.getName(),
                stop.getLatitude(),
                stop.getLongitude(),
                stop.getStopOrder(),
                stop.getEstimatedTime()
        );
    }

    public BusRouteResponse toBusRouteResponse(BusRoute route) {
        List<RouteStopResponse> stops = route.getStops() != null
                ? route.getStops().stream().map(this::toRouteStopResponse).toList()
                : List.of();

        return new BusRouteResponse(
                route.getId(),
                route.getName(),
                route.getDescription(),
                route.getBus() != null ? route.getBus().getId() : null,
                route.getBus() != null ? route.getBus().getLicensePlate() : null,
                route.isActive(),
                stops
        );
    }

    public ScheduleResponse toScheduleResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getPeriod(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getBusPickupTime(),
                schedule.getBusDropoffTime(),
                schedule.getBusRoute() != null ? schedule.getBusRoute().getId() : null,
                schedule.getBusRoute() != null ? schedule.getBusRoute().getName() : null,
                schedule.getStudent() != null ? schedule.getStudent().getId() : null,
                schedule.getStudent() != null ? schedule.getStudent().getName() : null
        );
    }
}
