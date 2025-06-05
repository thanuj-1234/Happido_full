// src/main/java/com/cabsy/backend/services/impl/RatingServiceImpl.java
package com.cabsy.backend.services.impl;

import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.Rating;
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.repositories.RatingRepository;
import com.cabsy.backend.repositories.RideRepository;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.RatingService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, RideRepository rideRepository,
                             UserRepository userRepository, DriverRepository driverRepository) {
        this.ratingRepository = ratingRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public Rating addRating(Long rideId, Long userId, Long driverId, Integer stars, String comment) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId)); // TODO: Custom exception
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Check if rating for this ride already exists
        if (ratingRepository.findByRideId(rideId).isPresent()) {
            throw new RuntimeException("Rating for this ride already exists."); // TODO: Custom exception
        }
        // Basic validation for stars
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5.");
        }

        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setUser(user);
        rating.setDriver(driver);
        rating.setStars(stars);
        rating.setComment(comment);
        rating.setRatingTime(LocalDateTime.now());

        // TODO: Update the average rating of the driver/user based on this new rating
        // This would involve fetching the driver/user, recalculating their average rating, and saving them.

        return ratingRepository.save(rating);
    }

    @Override
    public Optional<Rating> getRatingById(Long ratingId) {
        return ratingRepository.findById(ratingId);
    }

    @Override
    public List<Rating> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId);
    }

    @Override
    public List<Rating> getRatingsByDriverId(Long driverId) {
        return ratingRepository.findByDriverId(driverId);
    }
}