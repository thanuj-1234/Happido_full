// src/main/java/com/cabsy/backend/dtos/DriverResponseDTO.java
package com.cabsy.backend.dtos;

import com.cabsy.backend.models.DriverStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private DriverStatus status;
    private Double rating;
    // Removed: currentLocationLat
    // Removed: currentLocationLon
}