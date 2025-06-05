// src/main/java/com/cabsy/backend/dtos/RideRequestDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RideRequestDTO {
    @NotNull(message = "Pickup latitude cannot be empty")
    private Double pickupLat;

    @NotNull(message = "Pickup longitude cannot be empty")
    private Double pickupLon;

    @NotNull(message = "Destination latitude cannot be empty")
    private Double destinationLat;

    @NotNull(message = "Destination longitude cannot be empty")
    private Double destinationLon;

    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    private String pickupAddress;

    @Size(max = 255, message = "Destination address cannot exceed 255 characters")
    private String destinationAddress;

    // We might add a requested_cab_type here if multiple cab types exist
}