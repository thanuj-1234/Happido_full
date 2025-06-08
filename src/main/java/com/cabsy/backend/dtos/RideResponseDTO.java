// src/main/java/com/cabsy/backend/dtos/RideResponseDTO.java
package com.cabsy.backend.dtos;

import com.cabsy.backend.models.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDTO {
    private Long id;
    private Long userId;
    private Long driverId;
    private String pickupAddress;
    private String destinationAddress;
    private RideStatus status;
    private Double actualFare; // Still here
    private Double distance; // New: Distance of the ride
    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}