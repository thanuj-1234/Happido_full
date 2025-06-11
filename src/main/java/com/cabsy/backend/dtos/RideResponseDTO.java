// src/main/java/com/cabsy/backend/dtos/RideResponseDTO.java
package com.cabsy.backend.dtos;

import java.time.LocalDateTime;

import com.cabsy.backend.models.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for carrying ride response data from the server to the client.
 * Includes details of a ride, linking back to the associated driver and user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDTO {
    private Long id;
    private Long userId; // ID of the user who requested the ride
    private Long driverId; // ID of the driver who accepted the ride (can be null)
    private String pickupAddress;
    private String destinationAddress;
    private RideStatus status;
    private Double actualFare;
    private Double distance;
    private LocalDateTime requestTime;
    private LocalDateTime startTime; // When ride begins
    private LocalDateTime endTime; // When ride ends

    public void setActualFare(Double actualFare) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setId(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
