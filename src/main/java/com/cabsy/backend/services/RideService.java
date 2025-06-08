// src/main/java/com/cabsy/backend/services/RideService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Optional;

import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.RideStatus;

public interface RideService {
    // RideRequestDTO now contains actualFare
    RideResponseDTO requestRide(Long userId, RideRequestDTO rideRequest);
    RideResponseDTO assignDriverToRide(Long rideId, Long driverId, Long cabId);
    // Updated method signature to include driverId (optional)
    RideResponseDTO updateRideStatus(Long rideId, RideStatus newStatus, Long driverId);
    Optional<RideResponseDTO> getRideById(Long rideId);
    List<RideResponseDTO> getRidesByUserId(Long userId);
    List<RideResponseDTO> getRidesByDriverId(Long driverId);
    List<RideResponseDTO> getRidesByStatus(RideStatus status);
}