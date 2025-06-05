// src/main/java/com/cabsy/backend/repositories/RideRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByUserId(Long userId);
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByUserIdAndStatus(Long userId, RideStatus status);
    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);
}