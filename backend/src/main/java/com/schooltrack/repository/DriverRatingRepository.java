package com.schooltrack.repository;

import com.schooltrack.model.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DriverRatingRepository extends JpaRepository<DriverRating, Long> {
    List<DriverRating> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    @Query("SELECT AVG(r.rating) FROM DriverRating r WHERE r.driver.id = :driverId")
    Double findAverageRatingByDriverId(Long driverId);

    long countByDriverId(Long driverId);
}
