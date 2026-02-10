package com.schooltrack.repository;

import com.schooltrack.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStudentId(Long studentId);
    List<Schedule> findByBusRouteId(Long busRouteId);
}
