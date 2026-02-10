package com.schooltrack.controller;

import com.schooltrack.dto.BusResponse;
import com.schooltrack.model.Bus;
import com.schooltrack.repository.BusRepository;
import com.schooltrack.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusRepository busRepository;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<BusResponse>> getActiveBuses() {
        return ResponseEntity.ok(
                busRepository.findByActiveTrue().stream()
                        .map(dtoMapper::toBusResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> getBus(@PathVariable Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        return ResponseEntity.ok(dtoMapper.toBusResponse(bus));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<BusResponse> updateLocation(@PathVariable Long id,
                                                      @RequestParam Double latitude,
                                                      @RequestParam Double longitude) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        bus.setCurrentLatitude(latitude);
        bus.setCurrentLongitude(longitude);
        return ResponseEntity.ok(dtoMapper.toBusResponse(busRepository.save(bus)));
    }
}
