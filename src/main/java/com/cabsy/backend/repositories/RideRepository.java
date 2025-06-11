// src/main/java/com/cabsy/backend/repositories/RideRepository.java
package com.cabsy.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.RideStatus;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByUserId(Long userId);
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByUserIdAndStatus(Long userId, RideStatus status);
    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);
    // New method to find rides by both user ID and driver ID
    List<Ride> findByUserIdAndDriverId(Long userId, Long driverId);
}