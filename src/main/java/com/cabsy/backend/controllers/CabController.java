// src/main/java/com/cabsy/backend/controllers/CabController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse; // Import the ApiResponse DTO
import com.cabsy.backend.models.Cab;
import com.cabsy.backend.models.CabStatus;
import com.cabsy.backend.services.CabService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cabs")
public class CabController {

    private final CabService cabService;

    public CabController(CabService cabService) {
        this.cabService = cabService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cab>> createCab(@RequestBody Cab cab) {
        try {
            Cab createdCab = cabService.createCab(cab);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Cab created successfully", createdCab));
        } catch (RuntimeException e) {
            // This could be a specific exception like DuplicateLicensePlateException
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to create cab", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cab>> getCabById(@PathVariable Long id) {
        return cabService.getCabById(id)
                .map(cab -> ResponseEntity.ok(ApiResponse.success("Cab fetched successfully", cab)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Cab not found", "Cab with ID " + id + " does not exist")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cab>>> getAllCabs() {
        List<Cab> cabs = cabService.getAllCabs();
        return ResponseEntity.ok(ApiResponse.success("Cabs fetched successfully", cabs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cab>> updateCab(@PathVariable Long id, @RequestBody Cab cabDetails) {
        try {
            Cab updatedCab = cabService.updateCab(id, cabDetails);
            return ResponseEntity.ok(ApiResponse.success("Cab updated successfully", updatedCab));
        } catch (RuntimeException e) {
            // This could be ResourceNotFoundException or DuplicateLicensePlateException on update
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update cab", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCab(@PathVariable Long id) {
        try {
            cabService.deleteCab(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("Cab deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Failed to delete cab", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Cab>>> getCabsByStatus(@PathVariable CabStatus status) {
        List<Cab> cabs = cabService.getCabsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Cabs by status fetched successfully", cabs));
    }

    @PutMapping("/{cabId}/assign/{driverId}")
    public ResponseEntity<ApiResponse<Cab>> assignCabToDriver(@PathVariable Long cabId, @PathVariable Long driverId) {
        try {
            Cab updatedCab = cabService.assignCabToDriver(cabId, driverId);
            return ResponseEntity.ok(ApiResponse.success("Cab assigned to driver successfully", updatedCab));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to assign cab to driver", e.getMessage()));
        }
    }

    @PutMapping("/{cabId}/updateStatus")
    public ResponseEntity<ApiResponse<Cab>> updateCabStatus(@PathVariable Long cabId, @RequestParam CabStatus newStatus) {
        try {
            Cab updatedCab = cabService.updateCabStatus(cabId, newStatus);
            return ResponseEntity.ok(ApiResponse.success("Cab status updated successfully", updatedCab));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update cab status", e.getMessage()));
        }
    }
}