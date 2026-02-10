package com.schooltrack.controller;

import com.schooltrack.dto.ScheduleResponse;
import com.schooltrack.repository.ScheduleRepository;
import com.schooltrack.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        return ResponseEntity.ok(
                scheduleRepository.findAll().stream()
                        .map(dtoMapper::toScheduleResponse)
                        .toList()
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ScheduleResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(
                scheduleRepository.findByStudentId(studentId).stream()
                        .map(dtoMapper::toScheduleResponse)
                        .toList()
        );
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<ScheduleResponse>> getByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(
                scheduleRepository.findByBusRouteId(routeId).stream()
                        .map(dtoMapper::toScheduleResponse)
                        .toList()
        );
    }
}
