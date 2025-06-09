package com.cabsy.backend.services;

import com.cabsy.backend.dtos.RatingResponseDTO; // Import the new DTO
import com.cabsy.backend.models.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingService {
    RatingResponseDTO addRating(Long rideId, Long userId, Long driverId, Integer stars, String comment); // Change return type
    Optional<RatingResponseDTO> getRatingById(Long ratingId); // Change return type
    List<RatingResponseDTO> getRatingsByUserId(Long userId); // Change return type
    List<RatingResponseDTO> getRatingsByDriverId(Long driverId); // Change return type
}