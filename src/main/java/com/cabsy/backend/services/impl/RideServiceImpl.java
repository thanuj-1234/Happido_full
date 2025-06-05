// src/main/java/com/cabsy/backend/services/impl/RideServiceImpl.java
package com.cabsy.backend.services.impl;

import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.CabRepository;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.repositories.RideRepository;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.RideService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final CabRepository cabRepository;

    public RideServiceImpl(RideRepository rideRepository, UserRepository userRepository,
                           DriverRepository driverRepository, CabRepository cabRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.cabRepository = cabRepository;
    }

    @Override
    @Transactional
    public RideResponseDTO requestRide(Long userId, RideRequestDTO rideRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); // TODO: Custom exception

        Ride ride = new Ride();
        ride.setUser(user);
        ride.setPickupLat(rideRequest.getPickupLat());
        ride.setPickupLon(rideRequest.getPickupLon());
        ride.setDestinationLat(rideRequest.getDestinationLat());
        ride.setDestinationLon(rideRequest.getDestinationLon());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDestinationAddress(rideRequest.getDestinationAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimatedFare(rideRequest.getPickupLat(), rideRequest.getPickupLon(),
                                                    rideRequest.getDestinationLat(), rideRequest.getDestinationLon()));
        ride.setRequestTime(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);

        // TODO: Implement logic to find and assign a driver here or in a separate "matching" service
        // For now, we'll just return the requested ride.
        // In a real app, this would trigger driver notification and assignment.

        return mapToRideResponseDTO(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO assignDriverToRide(Long rideId, Long driverId, Long cabId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("Cab not found with id: " + cabId));

        // Basic checks:
        if (ride.getDriver() != null || ride.getCab() != null) {
            throw new RuntimeException("Ride already has a driver/cab assigned.");
        }
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available for a ride.");
        }
        if (cab.getStatus() != CabStatus.AVAILABLE) {
            throw new RuntimeException("Cab is not available.");
        }
        if (!driver.getCab().getId().equals(cabId)) {
             throw new RuntimeException("Assigned cab does not belong to the assigned driver.");
        }


        ride.setDriver(driver);
        ride.setCab(cab);
        ride.setStatus(RideStatus.ACCEPTED);
        driver.setStatus(DriverStatus.OCCUPIED); // Driver is now occupied
        cab.setStatus(CabStatus.ON_TRIP); // Cab is now on trip

        driverRepository.save(driver); // Update driver status
        cabRepository.save(cab);       // Update cab status
        Ride updatedRide = rideRepository.save(ride);

        return mapToRideResponseDTO(updatedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO updateRideStatus(Long rideId, RideStatus newStatus) {
        return rideRepository.findById(rideId).map(ride -> {
            // Add state transition validation here if needed (e.g., cannot go from COMPLETED to REQUESTED)
            ride.setStatus(newStatus);

            if (newStatus == RideStatus.IN_PROGRESS && ride.getStartTime() == null) {
                ride.setStartTime(LocalDateTime.now());
                // TODO: Update driver/cab status if not already ON_TRIP/OCCUPIED
            } else if (newStatus == RideStatus.COMPLETED && ride.getEndTime() == null) {
                ride.setEndTime(LocalDateTime.now());
                // Calculate final fare based on actual time/distance (if different from estimated)
                if (ride.getActualFare() == null) {
                    // This is a placeholder; actual calculation based on route taken.
                    ride.setActualFare(ride.getEstimatedFare() * 1.05); // Example: 5% more than estimated
                }
                // Set driver and cab back to AVAILABLE
                if (ride.getDriver() != null) {
                    ride.getDriver().setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(ride.getDriver());
                }
                if (ride.getCab() != null) {
                    ride.getCab().setStatus(CabStatus.AVAILABLE);
                    cabRepository.save(ride.getCab());
                }
            } else if (newStatus == RideStatus.CANCELLED) {
                 // Handle cancellation logic (e.g., free up driver/cab if assigned)
                 if (ride.getDriver() != null) {
                    ride.getDriver().setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(ride.getDriver());
                }
                if (ride.getCab() != null) {
                    ride.getCab().setStatus(CabStatus.AVAILABLE);
                    cabRepository.save(ride.getCab());
                }
            }

            Ride updatedRide = rideRepository.save(ride);
            return mapToRideResponseDTO(updatedRide);
        }).orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
    }

    @Override
    public Optional<RideResponseDTO> getRideById(Long rideId) {
        return rideRepository.findById(rideId).map(this::mapToRideResponseDTO);
    }

    @Override
    public List<RideResponseDTO> getRidesByUserId(Long userId) {
        return rideRepository.findByUserId(userId).stream()
                .map(this::mapToRideResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponseDTO> getRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(this::mapToRideResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Helper methods ---

    private Double calculateEstimatedFare(Double pickupLat, Double pickupLon, Double destLat, Double destLon) {
        // TODO: Implement a more sophisticated fare calculation based on distance, time, traffic, etc.
        // For now, a simple fixed rate per km (e.g., based on Haversine distance).
        // This is a placeholder.
        double distanceKm = calculateHaversineDistance(pickupLat, pickupLon, destLat, destLon);
        return distanceKm * 10.0; // Example: 10 units per km
    }

    private double calculateHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radius of Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private RideResponseDTO mapToRideResponseDTO(Ride ride) {
        return new RideResponseDTO(
            ride.getId(),
            ride.getUser() != null ? ride.getUser().getId() : null,
            ride.getDriver() != null ? ride.getDriver().getId() : null,
            ride.getCab() != null ? ride.getCab().getId() : null,
            ride.getPickupLat(),
            ride.getPickupLon(),
            ride.getDestinationLat(),
            ride.getDestinationLon(),
            ride.getPickupAddress(),
            ride.getDestinationAddress(),
            ride.getStatus(),
            ride.getEstimatedFare(),
            ride.getActualFare(),
            ride.getRequestTime(),
            ride.getStartTime(),
            ride.getEndTime()
        );
    }
}