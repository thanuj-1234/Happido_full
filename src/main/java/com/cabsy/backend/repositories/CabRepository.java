// src/main/java/com/cabsy/backend/repositories/CabRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CabRepository extends JpaRepository<Cab, Long> {
    Optional<Cab> findByLicensePlate(String licensePlate);
    List<Cab> findByStatus(CabStatus status);
}