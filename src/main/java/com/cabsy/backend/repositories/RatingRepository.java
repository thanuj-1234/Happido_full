// src/main/java/com/cabsy/backend/repositories/RatingRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByRideId(Long rideId);
    List<Rating> findByUserId(Long userId);
    List<Rating> findByDriverId(Long driverId);
}