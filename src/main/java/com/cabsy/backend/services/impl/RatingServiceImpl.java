package com.cabsy.backend.services.impl;

import com.cabsy.backend.dtos.RatingResponseDTO; // Import the new DTO
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
import java.util.stream.Collectors;

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
    public RatingResponseDTO addRating(Long rideId, Long userId, Long driverId, Integer stars, String comment) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        if (ratingRepository.findByRideId(rideId).isPresent()) {
            throw new RuntimeException("Rating for this ride already exists.");
        }
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

        Rating savedRating = ratingRepository.save(rating);

        // TODO: Update the average rating of the driver/user based on this new rating
        // This would involve fetching the driver/user, recalculating their average rating, and saving them.

        return convertToDTO(savedRating);
    }

    @Override
    public Optional<RatingResponseDTO> getRatingById(Long ratingId) {
        return ratingRepository.findById(ratingId).map(this::convertToDTO);
    }

    @Override
    public List<RatingResponseDTO> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingResponseDTO> getRatingsByDriverId(Long driverId) {
        return ratingRepository.findByDriverId(driverId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RatingResponseDTO convertToDTO(Rating rating) {
        if (rating == null) {
            return null;
        }
        return new RatingResponseDTO(
                rating.getId(),
                rating.getRide().getId(),
                rating.getUser().getId(),
                rating.getDriver().getId(),
                rating.getStars(),
                rating.getComment(),
                rating.getRatingTime()
        );
    }
}