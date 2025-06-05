// src/main/java/com/cabsy/backend/repositories/DriverRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Driver;
import com.cabsy.backend.models.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPhoneNumber(String phoneNumber);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    List<Driver> findByStatus(DriverStatus status);
    
}