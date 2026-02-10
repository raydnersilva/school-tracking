package com.schooltrack.controller;

import com.schooltrack.dto.BusRouteResponse;
import com.schooltrack.repository.BusRouteRepository;
import com.schooltrack.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final BusRouteRepository busRouteRepository;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<BusRouteResponse>> getActiveRoutes() {
        return ResponseEntity.ok(
                busRouteRepository.findByActiveTrue().stream()
                        .map(dtoMapper::toBusRouteResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusRouteResponse> getRoute(@PathVariable Long id) {
        var route = busRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada"));
        return ResponseEntity.ok(dtoMapper.toBusRouteResponse(route));
    }
}
