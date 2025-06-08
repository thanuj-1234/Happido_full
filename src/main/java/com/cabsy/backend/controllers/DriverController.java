// src/main/java/com/cabsy/backend/controllers/DriverController.java
package com.cabsy.backend.controllers;

import java.util.List;
import java.util.Map;

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

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.ResetPasswordRequestDTO; // NEW
import com.cabsy.backend.dtos.PasswordResetConfirmationDTO; // NEW
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.services.DriverService;

import jakarta.validation.Valid;

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

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverStatus>> getDriverStatus(@PathVariable Long id) {
        return driverService.getDriverStatus(id)
                .map(status -> ResponseEntity.ok(ApiResponse.success("Driver status fetched successfully", status)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Driver status not found", "Driver with ID " + id + " does not exist or status could not be retrieved")));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        try {
            DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Driver status updated successfully", updatedDriver));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Driver not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Failed to update driver status", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update driver status", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> updateDriverProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        try {
            DriverResponseDTO updatedDriver = driverService.updateDriverProfile(id, updates);
            return ResponseEntity.ok(ApiResponse.success("Driver profile updated successfully", updatedDriver));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Driver not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Update failed", e.getMessage()));
            } else if (e.getMessage().contains("already taken") || e.getMessage().contains("Invalid rating")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Update failed", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during profile update", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponseDTO>> registerDriver(@Valid @RequestBody DriverRegistrationDTO registrationDTO) {
        try {
            DriverResponseDTO registeredDriver = driverService.registerDriver(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Driver registered successfully", registeredDriver));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during registration", e.getMessage()));
        }
    }

   
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<PasswordResetConfirmationDTO>> forgotPassword(@Valid @RequestBody ResetPasswordRequestDTO resetRequest) {
        try {
            // In a real application, you'd likely send an email with a reset token here
            // For now, we'll directly call the service method to update the password
            PasswordResetConfirmationDTO confirmation = driverService.resetDriverPassword(resetRequest.getEmail(), resetRequest.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password reset request initiated. Check your email for further instructions (if tokens were implemented).", confirmation));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Driver not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Password reset failed", e.getMessage()));
            } else if (e.getMessage().contains("Weak password")) { // Example for password validation
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Password reset failed", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during password reset", e.getMessage()));
        }
    }
}