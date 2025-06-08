// src/main/java/com/cabsy/backend/services/impl/DriverServiceImpl.java
package com.cabsy.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.PasswordResetConfirmationDTO; // NEW import
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.services.DriverService;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverServiceImpl(DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public DriverResponseDTO registerDriver(DriverRegistrationDTO registrationDTO) {
        if (driverRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Driver with this email already exists"); // TODO: Custom exception
        }
        if (driverRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Driver with this phone number already exists"); // TODO: Custom exception
        }
        if (driverRepository.findByLicenseNumber(registrationDTO.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Driver with this license number already exists"); // TODO: Custom exception
        }

        Driver driver = new Driver();
        driver.setName(registrationDTO.getName());
        driver.setEmail(registrationDTO.getEmail());
        driver.setPhoneNumber(registrationDTO.getPhoneNumber());
        driver.setLicenseNumber(registrationDTO.getLicenseNumber());
        driver.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        driver.setStatus(DriverStatus.APPROVAL_PENDING);
        driver.setRating(0.0); // Default rating

        Driver savedDriver = driverRepository.save(driver);
        return convertToDto(savedDriver);
    }

    @Override
    public Optional<Driver> findDriverByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    @Override
    public Optional<DriverResponseDTO> getDriverById(Long id) {
        return driverRepository.findById(id)
                .map(this::convertToDto); // Use helper method
    }

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::convertToDto) // Use helper method
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId)); // TODO: Custom exception

        // Optional: Add business logic for valid status transitions here if needed
        driver.setStatus(newStatus);
        driver.setUpdatedAt(LocalDateTime.now()); // Ensure updated_at is set manually if @PreUpdate is not sufficient
        Driver updatedDriver = driverRepository.save(driver); // Persist changes
        return convertToDto(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponseDTO updateDriverProfile(Long id, Map<String, String> updates) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id)); // TODO: Custom exception

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    driver.setName(value);
                    break;
                case "email":
                    // Check for email uniqueness, allowing driver's own email
                    if (driverRepository.findByEmail(value).isPresent() && !driver.getEmail().equals(value)) {
                        throw new RuntimeException("Email already taken: " + value); // TODO: Custom exception
                    }
                    driver.setEmail(value);
                    break;
                case "phoneNumber":
                    // Check for phone number uniqueness
                    if (driverRepository.findByPhoneNumber(value).isPresent() && !driver.getPhoneNumber().equals(value)) {
                        throw new RuntimeException("Phone number already taken: " + value); // TODO: Custom exception
                    }
                    driver.setPhoneNumber(value);
                    break;
                case "licenseNumber":
                    // Check for license number uniqueness
                    if (driverRepository.findByLicenseNumber(value).isPresent() && !driver.getLicenseNumber().equals(value)) {
                        throw new RuntimeException("License number already taken: " + value); // TODO: Custom exception
                    }
                    driver.setLicenseNumber(value);
                    break;
                case "password":
                    // ALWAYS HASH THE PASSWORD BEFORE SAVING!
                    driver.setPassword(passwordEncoder.encode(value));
                    break;
                case "rating":
                    try {
                        Double newRating = Double.parseDouble(value);
                        // Optional: Add validation for rating range (e.g., 0.0 to 5.0)
                        if (newRating < 0.0 || newRating > 5.0) {
                            throw new RuntimeException("Rating must be between 0.0 and 5.0"); // TODO: Custom exception
                        }
                        driver.setRating(newRating);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid rating format: " + value); // TODO: Custom exception
                    }
                    break;
                default:
                    // Optionally log or throw an error for unsupported fields
                    System.out.println("Ignoring unknown or unsupported driver update field: " + key);
                    break;
            }
        });

        driver.setUpdatedAt(LocalDateTime.now()); // Update the timestamp manually
        Driver updatedDriver = driverRepository.save(driver); // Save the updated driver
        return convertToDto(updatedDriver); // Return the DTO
    }

    @Override
    public Optional<DriverStatus> getDriverStatus(Long id) {
        return driverRepository.findById(id)
                .map(Driver::getStatus); // Directly map to the status enum
    }

   
    @Override
    @Transactional
    public PasswordResetConfirmationDTO resetDriverPassword(String email, String newPassword) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));

        // Basic password validation:
        // - At least 8 characters
        // - At least one uppercase letter
        // - At least one lowercase letter
        // - At least one digit
        // - At least one special character
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!Pattern.matches(passwordRegex, newPassword)) {
            throw new RuntimeException("New password does not meet complexity requirements. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        // Encode the new password
        driver.setPassword(passwordEncoder.encode(newPassword));
        driver.setUpdatedAt(LocalDateTime.now()); // Update the timestamp
        driverRepository.save(driver); // Persist the updated password

        return new PasswordResetConfirmationDTO("Password for driver with email " + email + " has been successfully reset.", email);
    }

    // Helper method to convert Driver entity to DriverResponseDTO
    private DriverResponseDTO convertToDto(Driver driver) {
        return new DriverResponseDTO(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhoneNumber(),
                driver.getLicenseNumber(),
                driver.getStatus(),
                driver.getRating());
    }
}