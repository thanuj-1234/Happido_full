// src/main/java/com/cabsy/backend/controllers/DriverController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.DriverRegistrationDTO; // Assuming you have this DTO for registration
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.services.DriverService;
import jakarta.validation.Valid; // <--- NEW: For @Valid if you use it on registration DTO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // <--- NEW: For driver registration
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // <--- NEW: For @RequestBody in updateProfile
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map; // <--- NEW: Import Map for updateDriverProfile

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id)
                .map(driverDTO -> ResponseEntity.ok(ApiResponse.success("Driver fetched successfully", driverDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Driver not found", "Driver with ID " + id + " does not exist")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverResponseDTO>>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success("Drivers fetched successfully", drivers));
    }

    // Existing: Endpoint for updating driver status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        try {
            DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Driver status updated successfully", updatedDriver));
        } catch (RuntimeException e) {
            // TODO: Use custom exceptions from service for more specific error responses
            if (e.getMessage().contains("Driver not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Failed to update driver status", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update driver status", e.getMessage()));
        }
    }

    // <--- NEW: Endpoint for updating driver profile data --->
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> updateDriverProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) { // Accepts JSON body with key-value pairs
        try {
            DriverResponseDTO updatedDriver = driverService.updateDriverProfile(id, updates);
            return ResponseEntity.ok(ApiResponse.success("Driver profile updated successfully", updatedDriver));
        } catch (RuntimeException e) {
            // TODO: Use custom exceptions from service for more specific error responses
            if (e.getMessage().contains("Driver not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Update failed", e.getMessage()));
            } else if (e.getMessage().contains("already taken") || e.getMessage().contains("Invalid rating")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Update failed", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during profile update", e.getMessage()));
        }
    }

    // <--- NEW: Optional Driver Registration Endpoint --->
    // Assuming you have a DriverRegistrationDTO for this
    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponseDTO>> registerDriver(@Valid @RequestBody DriverRegistrationDTO registrationDTO) {
        try {
            DriverResponseDTO registeredDriver = driverService.registerDriver(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Driver registered successfully", registeredDriver));
        } catch (RuntimeException e) {
            // TODO: Handle specific registration exceptions (e.g., duplicate email/phone/license)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during registration", e.getMessage()));
        }
    }
}