// src/main/java/com/cabsy/backend/services/impl/CabServiceImpl.java
package com.cabsy.backend.services.impl;

import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import com.cabsy.backend.models.Driver;
import com.cabsy.backend.repositories.CabRepository;
import com.cabsy.backend.repositories.DriverRepository;
import com.cabsy.backend.services.CabService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CabServiceImpl implements CabService {

    private final CabRepository cabRepository;
    private final DriverRepository driverRepository; // Needed for assigning cabs to drivers

    public CabServiceImpl(CabRepository cabRepository, DriverRepository driverRepository) {
        this.cabRepository = cabRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public Cab createCab(Cab cab) {
        // Add checks for unique license plate if needed
        if (cabRepository.findByLicensePlate(cab.getLicensePlate()).isPresent()) {
            throw new RuntimeException("Cab with this license plate already exists"); // TODO: Custom exception
        }
        return cabRepository.save(cab);
    }

    @Override
    public Optional<Cab> getCabById(Long id) {
        return cabRepository.findById(id);
    }

    @Override
    public List<Cab> getAllCabs() {
        return cabRepository.findAll();
    }

    @Override
    @Transactional
    public Cab updateCab(Long id, Cab cabDetails) {
        return cabRepository.findById(id).map(cab -> {
            cab.setLicensePlate(cabDetails.getLicensePlate());
            cab.setModel(cabDetails.getModel());
            cab.setColor(cabDetails.getColor());
            cab.setCapacity(cabDetails.getCapacity());
            cab.setStatus(cabDetails.getStatus());
            // Do not update driver here directly, use assignCabToDriver method
            return cabRepository.save(cab);
        }).orElseThrow(() -> new RuntimeException("Cab not found with id: " + id)); // TODO: Custom exception
    }

    @Override
    @Transactional
    public void deleteCab(Long id) {
        cabRepository.deleteById(id);
    }

    @Override
    public Optional<Cab> findCabByLicensePlate(String licensePlate) {
        return cabRepository.findByLicensePlate(licensePlate);
    }

    @Override
    public List<Cab> getCabsByStatus(CabStatus status) {
        return cabRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Cab assignCabToDriver(Long cabId, Long driverId) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("Cab not found with id: " + cabId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Check if driver already has a cab or cab is already assigned
        if (driver.getCab() != null && !driver.getCab().getId().equals(cabId)) {
            throw new RuntimeException("Driver already has a different cab assigned.");
        }
        if (cab.getDriver() != null && !cab.getDriver().getId().equals(driverId)) {
             throw new RuntimeException("Cab is already assigned to a different driver.");
        }

        cab.setDriver(driver);
        driver.setCab(cab); // Maintain bidirectional relationship
        driverRepository.save(driver); // Save driver to update cab relationship
        return cabRepository.save(cab);
    }

    @Override
    @Transactional
    public Cab updateCabStatus(Long cabId, CabStatus newStatus) {
        return cabRepository.findById(cabId).map(cab -> {
            cab.setStatus(newStatus);
            return cabRepository.save(cab);
        }).orElseThrow(() -> new RuntimeException("Cab not found with id: " + cabId)); // TODO: Custom exception
    }
}