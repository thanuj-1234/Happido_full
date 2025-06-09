// src/main/java/com/cabsy/backend/services/impl/RideServiceImpl.java
package com.cabsy.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.repositories.RideRepository;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.RideService;
import com.cabsy.backend.services.PaymentService; // Import PaymentService

import jakarta.transaction.Transactional;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PaymentService paymentService; // Inject PaymentService

    public RideServiceImpl(RideRepository rideRepository, UserRepository userRepository,
                           DriverRepository driverRepository, PaymentService paymentService) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public RideResponseDTO requestRide(Long userId, RideRequestDTO rideRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); // TODO: Custom exception

        Ride ride = new Ride();
        ride.setUser(user);
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDestinationAddress(rideRequest.getDestinationAddress());
        ride.setActualFare(rideRequest.getActualFare()); // Set actualFare from the request
        ride.setDistance(rideRequest.getDistance()); // New: Set distance from the request
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestTime(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);

        // TODO: Implement logic to find and assign a driver here or in a separate "matching" service
        // For now, this just saves the request. In a real app, this would trigger driver notification and assignment.

        return mapToRideResponseDTO(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO assignDriverToRide(Long rideId, Long driverId, Long cabId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        // Cab cab = cabRepository.findById(cabId)
        //       .orElseThrow(() -> new RuntimeException("Cab not found with id: " + cabId));

        // Basic checks:
        if (ride.getDriver() != null) { // More specific check: if a driver is already assigned
            throw new RuntimeException("Ride already has a driver assigned.");
        }
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available for a ride.");
        }
        // if (cab.getStatus() != CabStatus.AVAILABLE) {
        //  throw new RuntimeException("Cab is not available.");
        // }
        // if (!driver.getCab().getId().equals(cabId)) {
        //  throw new RuntimeException("Assigned cab does not belong to the assigned driver.");
        // }


        ride.setDriver(driver);
        // ride.setCab(cab);
        ride.setStatus(RideStatus.ACCEPTED);
        driver.setStatus(DriverStatus.OCCUPIED); // Driver is now occupied
        // cab.setStatus(CabStatus.ON_TRIP); // Cab is now on trip

        driverRepository.save(driver); // Update driver status
        // cabRepository.save(cab);        // Update cab status
        Ride updatedRide = rideRepository.save(ride);

        return mapToRideResponseDTO(updatedRide);
    }

    @Override
    @Transactional
    public RideResponseDTO updateRideStatus(Long rideId, RideStatus newStatus, Long driverId) {
        return rideRepository.findById(rideId).map(ride -> {
            // Add state transition validation here if needed (e.g., cannot go from COMPLETED to REQUESTED)

            // --- Logic to set driverId when status is COMPLETED or if not already set ---
            // This prioritizes the 'assignDriverToRide' for initial assignment.
            // This block handles cases where driverId might be passed during status update
            // especially if the ride never went through the 'assignDriverToRide' flow,
            // or if a driver wants to ensure their ID is recorded on completion.
            if (driverId != null) {
                // If ride doesn't have a driver assigned yet, assign this driver.
                // Or, if the driver is already assigned, confirm it's the same driver.
                if (ride.getDriver() == null) {
                    Driver driver = driverRepository.findById(driverId)
                            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
                    ride.setDriver(driver);
                    // Optionally, update driver status here if this is the point of assignment
                    // e.g., if status is IN_PROGRESS, driver might become OCCUPIED
                } else if (!ride.getDriver().getId().equals(driverId)) {
                    // This is a crucial business rule decision:
                    // If a driver is already assigned, and a *different* driverId is passed,
                    // should it be allowed? Usually not for simple status updates.
                    // For now, we'll throw an error. You might adjust this.
                    // Or, you might allow it only if the current driver is "unassigned" first.
                    throw new RuntimeException("Cannot change assigned driver via status update for ride ID: " + rideId);
                }
            }
            // --- End of driverId setting logic ---


            ride.setStatus(newStatus);

            if (newStatus == RideStatus.IN_PROGRESS && ride.getStartTime() == null) {
                ride.setStartTime(LocalDateTime.now());
                // If a driver was assigned during this status update to IN_PROGRESS,
                // ensure their status is set to OCCUPIED.
                if (ride.getDriver() != null && ride.getDriver().getStatus() != DriverStatus.OCCUPIED) {
                    ride.getDriver().setStatus(DriverStatus.OCCUPIED);
                    driverRepository.save(ride.getDriver());
                }
            } else if (newStatus == RideStatus.COMPLETED && ride.getEndTime() == null) {
                // This is the line that sets the end time when the ride is completed.
                ride.setEndTime(LocalDateTime.now());
                // actualFare is assumed to be set at the time of request, not here.

                // Create Payment record
                paymentService.createPayment(rideId, ride.getActualFare(), "Cash"); // Or get payment method from driver input

                // Set driver and cab back to AVAILABLE
                if (ride.getDriver() != null) {
                    ride.getDriver().setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(ride.getDriver());
                }
                // if (ride.getCab() != null) {
                //  ride.getCab().setStatus(CabStatus.AVAILABLE);
                //  cabRepository.save(ride.getCab());
                // }
            } else if (newStatus == RideStatus.CANCELLED) {
                // Handle cancellation logic (e.g., free up driver/cab if assigned)
                if (ride.getDriver() != null) {
                    ride.getDriver().setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(ride.getDriver());
                }
                // if (ride.getCab() != null) {
                //  ride.getCab().setStatus(CabStatus.AVAILABLE);
                //  cabRepository.save(ride.getCab());
                // }
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

    // --- New method: Get rides by status ---
    @Override
    public List<RideResponseDTO> getRidesByStatus(RideStatus status) {
        return rideRepository.findByStatus(status).stream()
                .map(this::mapToRideResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Helper method ---

    private RideResponseDTO mapToRideResponseDTO(Ride ride) {
        return new RideResponseDTO(
                ride.getId(),
                ride.getUser() != null ? ride.getUser().getId() : null,
                ride.getDriver() != null ? ride.getDriver().getId() : null,
                ride.getPickupAddress(),
                ride.getDestinationAddress(),
                ride.getStatus(),
                ride.getActualFare(),
                ride.getDistance(), // Include distance
                ride.getRequestTime(),
                ride.getStartTime(),
                ride.getEndTime()
        );
    }
}