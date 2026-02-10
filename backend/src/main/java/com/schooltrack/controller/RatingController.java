package com.schooltrack.controller;

import com.schooltrack.dto.DriverRatingRequest;
import com.schooltrack.dto.DriverRatingResponse;
import com.schooltrack.model.DriverRating;
import com.schooltrack.repository.DriverRatingRepository;
import com.schooltrack.repository.TripHistoryRepository;
import com.schooltrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final DriverRatingRepository driverRatingRepository;
    private final UserRepository userRepository;
    private final TripHistoryRepository tripHistoryRepository;

    @PostMapping
    public ResponseEntity<DriverRatingResponse> rateDriver(
            @RequestBody DriverRatingRequest request, Authentication authentication) {
        var parent = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        DriverRating rating = DriverRating.builder()
                .driver(driver)
                .parent(parent)
                .rating(Math.min(5, Math.max(1, request.rating())))
                .comment(request.comment())
                .build();

        if (request.tripId() != null) {
            tripHistoryRepository.findById(request.tripId()).ifPresent(rating::setTrip);
        }

        driverRatingRepository.save(rating);

        return ResponseEntity.ok(new DriverRatingResponse(
                rating.getId(),
                driver.getId(),
                driver.getName(),
                parent.getId(),
                parent.getName(),
                rating.getRating(),
                rating.getComment(),
                rating.getCreatedAt()
        ));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverRatings(@PathVariable Long driverId) {
        Double avg = driverRatingRepository.findAverageRatingByDriverId(driverId);
        long count = driverRatingRepository.countByDriverId(driverId);

        var ratings = driverRatingRepository.findByDriverIdOrderByCreatedAtDesc(driverId).stream()
                .map(r -> new DriverRatingResponse(
                        r.getId(), r.getDriver().getId(), r.getDriver().getName(),
                        r.getParent().getId(), r.getParent().getName(),
                        r.getRating(), r.getComment(), r.getCreatedAt()
                )).toList();

        return ResponseEntity.ok(Map.of(
                "average", avg != null ? avg : 0.0,
                "count", count,
                "ratings", ratings
        ));
    }
}
