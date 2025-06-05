// src/main/java/com/cabsy/backend/services/DriverService.java
package com.cabsy.backend.services;

import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import java.util.List;
import java.util.Map; // <--- NEW: Import Map for updateDriverProfile
import java.util.Optional;

public interface DriverService {
    DriverResponseDTO registerDriver(DriverRegistrationDTO registrationDTO);
    Optional<Driver> findDriverByEmail(String email);
    Optional<DriverResponseDTO> getDriverById(Long id);
    List<DriverResponseDTO> getAllDrivers();

    // Already exists: Update driver status
    DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus);

    // <--- NEW: Method for updating driver profile with a map of changes --->
    DriverResponseDTO updateDriverProfile(Long id, Map<String, String> updates);
}