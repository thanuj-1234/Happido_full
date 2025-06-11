// src/main/java/com/cabsy/backend/controllers/RideController.java
package com.cabsy.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // This DTO will now carry 'status' and optionally 'driverId'
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.RideRequestDTO;
import com.cabsy.backend.dtos.RideResponseDTO;
import com.cabsy.backend.dtos.RideStatusUpdateDTO;
import com.cabsy.backend.models.RideStatus;
import com.cabsy.backend.services.RideService;

import jakarta.validation.Valid;

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
            @Valid @RequestBody RideRequestDTO rideRequestDTO) { // RideRequestDTO now contains actualFare
        try {
            RideResponseDTO newRide = rideService.requestRide(userId, rideRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Ride requested successfully", newRide));
        } catch (RuntimeException e) {
            // Consider more specific exception handling and logging for production
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
            @Valid @RequestBody RideStatusUpdateDTO updateDTO) { // RideStatusUpdateDTO now carries 'status' and optionally 'driverId'
        try {
            // Pass the status and driverId to the service.
            RideResponseDTO updatedRide = rideService.updateRideStatus(rideId, updateDTO.getStatus(), updateDTO.getDriverId());
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

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getRidesByStatus(@PathVariable RideStatus status) {
        List<RideResponseDTO> rides = rideService.getRidesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Rides with status " + status + " fetched successfully", rides));
    }

    // New API endpoint to fetch rides by both userId and driverId
    @GetMapping("/user/{userId}/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<RideResponseDTO>>> getRidesByUserIdAndDriverId(
            @PathVariable Long userId,
            @PathVariable Long driverId) {
        List<RideResponseDTO> rides = rideService.getRidesByUserIdAndDriverId(userId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Rides for user and driver fetched successfully", rides));
    }
}