// src/main/java/com/cabsy/backend/controllers/RideController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.models.RideStatus;
import com.cabsy.backend.services.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<ApiResponse<RideResponseDTO>> requestRide(
            @PathVariable Long userId,
            @Valid @RequestBody RideRequestDTO rideRequestDTO) {
        try {
            RideResponseDTO newRide = rideService.requestRide(userId, rideRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Ride requested successfully", newRide));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to request ride", e.getMessage()));
        }
    }

    @PutMapping("/{rideId}/assign")
    public ResponseEntity<ApiResponse<RideResponseDTO>> assignDriverToRide(
            @PathVariable Long rideId,
            @RequestParam Long driverId,
            @RequestParam Long cabId) {
        try {
            RideResponseDTO assignedRide = rideService.assignDriverToRide(rideId, driverId, cabId);
            return ResponseEntity.ok(ApiResponse.success("Driver and cab assigned to ride", assignedRide));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to assign driver/cab", e.getMessage()));
        }
    }

    @PutMapping("/{rideId}/status")
    public ResponseEntity<ApiResponse<RideResponseDTO>> updateRideStatus(
            @PathVariable Long rideId,
            @RequestParam RideStatus status) {
        try {
            RideResponseDTO updatedRide = rideService.updateRideStatus(rideId, status);
            return ResponseEntity.ok(ApiResponse.success("Ride status updated successfully", updatedRide));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update ride status", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideResponseDTO>> getRideById(@PathVariable Long id) {
        return rideService.getRideById(id)
                .map(rideDTO -> ResponseEntity.ok(ApiResponse.success("Ride fetched successfully", rideDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Ride not found", "Ride with ID " + id + " does not exist")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getRidesByUserId(@PathVariable Long userId) {
        List<RideResponseDTO> rides = rideService.getRidesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Rides for user fetched successfully", rides));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getRidesByDriverId(@PathVariable Long driverId) {
        List<RideResponseDTO> rides = rideService.getRidesByDriverId(driverId);
        return ResponseEntity.ok(ApiResponse.success("Rides for driver fetched successfully", rides));
    }
}