// src/main/java/com/cabsy/backend/services/CabService.java
package com.cabsy.backend.services;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import java.util.List;
import java.util.Optional;

public interface CabService {
    Cab createCab(Cab cab);
    Optional<Cab> getCabById(Long id);
    List<Cab> getAllCabs();
    Cab updateCab(Long id, Cab cabDetails);
    void deleteCab(Long id);
    Optional<Cab> findCabByLicensePlate(String licensePlate);
    List<Cab> getCabsByStatus(CabStatus status);
    Cab assignCabToDriver(Long cabId, Long driverId);
    Cab updateCabStatus(Long cabId, CabStatus newStatus);
}