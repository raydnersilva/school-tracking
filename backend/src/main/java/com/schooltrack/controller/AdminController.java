package com.schooltrack.controller;

import com.schooltrack.dto.*;
import com.schooltrack.model.*;
import com.schooltrack.repository.*;
import com.schooltrack.service.DtoMapper;
import com.schooltrack.service.EtaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;
    private final TripHistoryRepository tripHistoryRepository;
    private final DriverRatingRepository driverRatingRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper dtoMapper;
    private final EtaService etaService;

    // === Dashboard Stats ===
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        long tripsToday = tripHistoryRepository.findByStartedAtBetween(todayStart, todayEnd).size();
        long tripsCompleted = tripHistoryRepository.countByStatus(TripHistory.TripStatus.COMPLETED);

        Double avgRating = null;
        var drivers = userRepository.findByRole(Role.DRIVER);
        if (!drivers.isEmpty()) {
            double sum = 0;
            int count = 0;
            for (var d : drivers) {
                Double r = driverRatingRepository.findAverageRatingByDriverId(d.getId());
                if (r != null) { sum += r; count++; }
            }
            if (count > 0) avgRating = Math.round(sum / count * 10.0) / 10.0;
        }

        return ResponseEntity.ok(new DashboardStatsResponse(
                studentRepository.count(),
                busRepository.count(),
                busRepository.findByActiveTrue().size(),
                busRouteRepository.count(),
                busRouteRepository.findByActiveTrue().size(),
                userRepository.findByRole(Role.DRIVER).size(),
                tripsToday,
                tripsCompleted,
                avgRating
        ));
    }

    // === Users CRUD ===
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream().map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "name", u.getName(),
                        "email", u.getEmail(),
                        "role", u.getRole().name()
                )).toList()
        );
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> body) {
        User user = User.builder()
                .username(body.get("username"))
                .name(body.get("name"))
                .email(body.get("email"))
                .password(passwordEncoder.encode(body.get("password")))
                .role(Role.valueOf(body.getOrDefault("role", "PARENT").toUpperCase()))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Buses CRUD ===
    @GetMapping("/buses")
    public ResponseEntity<List<BusResponse>> getAllBuses() {
        return ResponseEntity.ok(
                busRepository.findAll().stream().map(dtoMapper::toBusResponse).toList()
        );
    }

    @PostMapping("/buses")
    public ResponseEntity<BusResponse> createBus(@RequestBody Map<String, Object> body) {
        Bus bus = new Bus();
        bus.setLicensePlate((String) body.get("licensePlate"));
        bus.setCapacity((Integer) body.get("capacity"));
        bus.setModel((String) body.get("model"));
        bus.setActive(true);

        if (body.containsKey("driverId")) {
            Long driverId = Long.valueOf(body.get("driverId").toString());
            userRepository.findById(driverId).ifPresent(bus::setDriver);
        }

        busRepository.save(bus);
        return ResponseEntity.ok(dtoMapper.toBusResponse(bus));
    }

    @PutMapping("/buses/{id}")
    public ResponseEntity<BusResponse> updateBus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));

        if (body.containsKey("licensePlate")) bus.setLicensePlate((String) body.get("licensePlate"));
        if (body.containsKey("capacity")) bus.setCapacity((Integer) body.get("capacity"));
        if (body.containsKey("model")) bus.setModel((String) body.get("model"));
        if (body.containsKey("active")) bus.setActive((Boolean) body.get("active"));

        busRepository.save(bus);
        return ResponseEntity.ok(dtoMapper.toBusResponse(bus));
    }

    @DeleteMapping("/buses/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Routes CRUD ===
    @GetMapping("/routes")
    public ResponseEntity<List<BusRouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(
                busRouteRepository.findAll().stream().map(dtoMapper::toBusRouteResponse).toList()
        );
    }

    @PostMapping("/routes")
    public ResponseEntity<BusRouteResponse> createRoute(@RequestBody Map<String, Object> body) {
        BusRoute route = new BusRoute();
        route.setName((String) body.get("name"));
        route.setDescription((String) body.get("description"));
        route.setActive(true);

        if (body.containsKey("busId")) {
            Long busId = Long.valueOf(body.get("busId").toString());
            busRepository.findById(busId).ifPresent(route::setBus);
        }

        busRouteRepository.save(route);
        return ResponseEntity.ok(dtoMapper.toBusRouteResponse(route));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        busRouteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Students CRUD ===
    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(
                studentRepository.findAll().stream().map(dtoMapper::toStudentResponse).toList()
        );
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> createStudent(@RequestBody Map<String, Object> body) {
        Student student = new Student();
        student.setName((String) body.get("name"));
        student.setGrade((String) body.get("grade"));
        student.setSchool((String) body.get("school"));

        if (body.containsKey("parentId")) {
            Long parentId = Long.valueOf(body.get("parentId").toString());
            userRepository.findById(parentId).ifPresent(student::setParent);
        }
        if (body.containsKey("busRouteId")) {
            Long routeId = Long.valueOf(body.get("busRouteId").toString());
            busRouteRepository.findById(routeId).ifPresent(student::setBusRoute);
        }

        studentRepository.save(student);
        return ResponseEntity.ok(dtoMapper.toStudentResponse(student));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Trips History ===
    @GetMapping("/trips")
    public ResponseEntity<List<TripHistoryResponse>> getAllTrips() {
        return ResponseEntity.ok(
                tripHistoryRepository.findAll().stream().map(t -> new TripHistoryResponse(
                        t.getId(),
                        t.getBusRoute().getId(),
                        t.getBusRoute().getName(),
                        t.getDriver().getId(),
                        t.getDriver().getName(),
                        t.getStartedAt(),
                        t.getEndedAt(),
                        t.getStatus().name(),
                        t.getStudentsBoarded(),
                        t.getStudentsDropped(),
                        t.getDistanceKm(),
                        t.getDurationMinutes()
                )).toList()
        );
    }

    // === ETA ===
    @GetMapping("/eta/{busId}")
    public ResponseEntity<List<EtaResponse>> getEta(@PathVariable Long busId) {
        var bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));

        var routes = busRouteRepository.findAll().stream()
                .filter(r -> r.getBus() != null && r.getBus().getId().equals(busId))
                .findFirst();

        if (routes.isEmpty() || routes.get().getStops() == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(etaService.calculateEta(busId, routes.get().getStops()));
    }

    // === Driver Ratings ===
    @GetMapping("/ratings/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverRatings(@PathVariable Long driverId) {
        var ratings = driverRatingRepository.findByDriverIdOrderByCreatedAtDesc(driverId);
        Double avg = driverRatingRepository.findAverageRatingByDriverId(driverId);
        long count = driverRatingRepository.countByDriverId(driverId);

        return ResponseEntity.ok(Map.of(
                "average", avg != null ? avg : 0,
                "count", count,
                "ratings", ratings.stream().map(r -> new DriverRatingResponse(
                        r.getId(),
                        r.getDriver().getId(),
                        r.getDriver().getName(),
                        r.getParent().getId(),
                        r.getParent().getName(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                )).toList()
        ));
    }

    // === Notifications (broadcast) ===
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<Map<String, String>> broadcastNotification(@RequestBody Map<String, String> body) {
        var users = userRepository.findAll();
        for (var user : users) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title(body.get("title"))
                    .message(body.get("message"))
                    .type(body.getOrDefault("type", "info"))
                    .build();
            notificationRepository.save(notification);
        }
        return ResponseEntity.ok(Map.of("message", "Notificação enviada para " + users.size() + " usuários"));
    }
}
