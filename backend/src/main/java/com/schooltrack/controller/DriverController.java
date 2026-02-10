package com.schooltrack.controller;

import com.schooltrack.dto.BoardingRecordResponse;
import com.schooltrack.dto.TripHistoryResponse;
import com.schooltrack.model.*;
import com.schooltrack.repository.*;
import com.schooltrack.service.GeofenceService;
import com.schooltrack.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final TripHistoryRepository tripHistoryRepository;
    private final BoardingRecordRepository boardingRecordRepository;
    private final UserRepository userRepository;
    private final BusRouteRepository busRouteRepository;
    private final StudentRepository studentRepository;
    private final QrCodeService qrCodeService;
    private final GeofenceService geofenceService;

    @PostMapping("/trip/start")
    public ResponseEntity<TripHistoryResponse> startTrip(
            @RequestParam Long busRouteId, Authentication authentication) {
        var driver = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        var existing = tripHistoryRepository.findByDriverIdAndStatus(driver.getId(), TripHistory.TripStatus.IN_PROGRESS);
        if (existing.isPresent()) {
            throw new RuntimeException("Já existe uma viagem em andamento");
        }

        var route = busRouteRepository.findById(busRouteId)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada"));

        TripHistory trip = TripHistory.builder()
                .busRoute(route)
                .driver(driver)
                .startedAt(LocalDateTime.now())
                .status(TripHistory.TripStatus.IN_PROGRESS)
                .studentsBoarded(0)
                .studentsDropped(0)
                .build();

        tripHistoryRepository.save(trip);
        return ResponseEntity.ok(toTripResponse(trip));
    }

    @PutMapping("/trip/{tripId}/end")
    public ResponseEntity<TripHistoryResponse> endTrip(@PathVariable Long tripId) {
        var trip = tripHistoryRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));

        trip.setEndedAt(LocalDateTime.now());
        trip.setStatus(TripHistory.TripStatus.COMPLETED);
        trip.setDurationMinutes((int) ChronoUnit.MINUTES.between(trip.getStartedAt(), trip.getEndedAt()));

        tripHistoryRepository.save(trip);
        return ResponseEntity.ok(toTripResponse(trip));
    }

    @GetMapping("/trip/current")
    public ResponseEntity<TripHistoryResponse> getCurrentTrip(Authentication authentication) {
        var driver = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        var trip = tripHistoryRepository.findByDriverIdAndStatus(driver.getId(), TripHistory.TripStatus.IN_PROGRESS)
                .orElseThrow(() -> new RuntimeException("Nenhuma viagem em andamento"));

        return ResponseEntity.ok(toTripResponse(trip));
    }

    @PostMapping("/trip/{tripId}/board")
    public ResponseEntity<BoardingRecordResponse> boardStudent(
            @PathVariable Long tripId,
            @RequestParam Long studentId,
            @RequestParam(required = false) String qrCode,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {

        var trip = tripHistoryRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (qrCode != null) {
            Long validatedStudentId = qrCodeService.validateQrCode(qrCode);
            if (validatedStudentId == null || !validatedStudentId.equals(studentId)) {
                throw new RuntimeException("QR Code inválido");
            }
            qrCodeService.invalidateQrCode(qrCode);
        }

        BoardingRecord record = BoardingRecord.builder()
                .trip(trip)
                .student(student)
                .type(BoardingRecord.BoardingType.BOARDED)
                .qrCode(qrCode)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        boardingRecordRepository.save(record);
        trip.setStudentsBoarded(trip.getStudentsBoarded() + 1);
        tripHistoryRepository.save(trip);

        return ResponseEntity.ok(toBoardingResponse(record));
    }

    @PostMapping("/trip/{tripId}/drop")
    public ResponseEntity<BoardingRecordResponse> dropStudent(
            @PathVariable Long tripId,
            @RequestParam Long studentId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {

        var trip = tripHistoryRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        BoardingRecord record = BoardingRecord.builder()
                .trip(trip)
                .student(student)
                .type(BoardingRecord.BoardingType.DROPPED)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        boardingRecordRepository.save(record);
        trip.setStudentsDropped(trip.getStudentsDropped() + 1);
        tripHistoryRepository.save(trip);

        return ResponseEntity.ok(toBoardingResponse(record));
    }

    @GetMapping("/trip/{tripId}/boarding")
    public ResponseEntity<List<BoardingRecordResponse>> getTripBoarding(@PathVariable Long tripId) {
        return ResponseEntity.ok(
                boardingRecordRepository.findByTripIdOrderByTimestampAsc(tripId).stream()
                        .map(this::toBoardingResponse)
                        .toList()
        );
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripHistoryResponse>> getMyTrips(Authentication authentication) {
        var driver = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        return ResponseEntity.ok(
                tripHistoryRepository.findByDriverIdOrderByStartedAtDesc(driver.getId()).stream()
                        .map(this::toTripResponse)
                        .toList()
        );
    }

    @GetMapping("/students/qr/{studentId}")
    public ResponseEntity<Map<String, String>> generateStudentQr(@PathVariable Long studentId) {
        String qrCode = qrCodeService.generateQrCode(studentId);
        return ResponseEntity.ok(Map.of("qrCode", qrCode, "studentId", studentId.toString()));
    }

    private TripHistoryResponse toTripResponse(TripHistory trip) {
        return new TripHistoryResponse(
                trip.getId(),
                trip.getBusRoute().getId(),
                trip.getBusRoute().getName(),
                trip.getDriver().getId(),
                trip.getDriver().getName(),
                trip.getStartedAt(),
                trip.getEndedAt(),
                trip.getStatus().name(),
                trip.getStudentsBoarded(),
                trip.getStudentsDropped(),
                trip.getDistanceKm(),
                trip.getDurationMinutes()
        );
    }

    private BoardingRecordResponse toBoardingResponse(BoardingRecord record) {
        return new BoardingRecordResponse(
                record.getId(),
                record.getTrip().getId(),
                record.getStudent().getId(),
                record.getStudent().getName(),
                record.getType().name(),
                record.getTimestamp(),
                record.getLatitude(),
                record.getLongitude()
        );
    }
}
