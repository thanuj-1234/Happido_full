// src/main/java/com/cabsy/backend/services/DriverService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.PasswordResetConfirmationDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;


public interface DriverService {
    DriverResponseDTO registerDriver(DriverRegistrationDTO registrationDTO);
    Optional<Driver> findDriverByEmail(String email);
    Optional<DriverResponseDTO> getDriverById(Long id);
    List<DriverResponseDTO> getAllDrivers();

    // Already exists: Update driver status
    DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus);

    // Method for updating driver profile with a map of changes
    DriverResponseDTO updateDriverProfile(Long id, Map<String, String> updates);

    Optional<DriverStatus> getDriverStatus(Long id);

    /**
     * Resets the password for a driver identified by their email.
     * In a real-world scenario, this would typically involve
     * sending a password reset token to the email.
     *
     * @param email The email of the driver whose password needs to be reset.
     * @param newPassword The new password to set for the driver.
     * @return A DTO confirming the password reset.
     * @throws RuntimeException if the driver is not found or if the new password does not meet criteria.
     */
    PasswordResetConfirmationDTO resetDriverPassword(String email, String newPassword);
}