// src/main/java/com/cabsy/backend/repositories/DriverRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus; // Import DriverStatus enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;    // Import for List return type
import java.util.Optional; // Import for Optional return type

/**
 * Spring Data JPA Repository for Driver entities.
 * Provides standard CRUD operations and custom query methods for drivers.
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Finds a driver by their unique email address.
     * @param email The email address to search for.
     * @return An Optional containing the Driver entity if found, otherwise empty.
     */
    Optional<Driver> findByEmail(String email);

    /**
     * Finds a driver by their unique phone number.
     * @param phoneNumber The phone number to search for.
     * @return An Optional containing the Driver entity if found, otherwise empty.
     */
    Optional<Driver> findByPhoneNumber(String phoneNumber);

    /**
     * Finds a driver by their unique license number.
     * @param licenseNumber The license number to search for.
     * @return An Optional containing the Driver entity if found, otherwise empty.
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    /**
     * Finds all drivers with a specific status.
     * @param status The DriverStatus to filter by (e.g., AVAILABLE, OFFLINE).
     * @return A List of Driver entities matching the given status.
     */
    List<Driver> findByStatus(DriverStatus status);

    // Optional: If you implement a password reset token flow, uncomment and use this
    // Optional<Driver> findByResetToken(String resetToken);
}
