// src/main/java/com/cabsy/backend/services/RatingService.java
package com.cabsy.backend.services;

import com.cabsy.backend.models.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingService {
    Rating addRating(Long rideId, Long userId, Long driverId, Integer stars, String comment);
    Optional<Rating> getRatingById(Long ratingId);
    List<Rating> getRatingsByUserId(Long userId);
    List<Rating> getRatingsByDriverId(Long driverId);
}