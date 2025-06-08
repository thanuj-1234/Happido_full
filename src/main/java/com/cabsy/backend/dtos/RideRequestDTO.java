// src/main/java/com/cabsy/backend/dtos/RideRequestDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // For non-negative fare
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RideRequestDTO {

    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    @NotNull(message = "Pickup address is required") // Added validation
    private String pickupAddress;

    @Size(max = 255, message = "Destination address cannot exceed 255 characters")
    @NotNull(message = "Destination address is required") // Added validation
    private String destinationAddress;

    @NotNull(message = "Actual fare is required at the time of request")
    @PositiveOrZero(message = "Actual fare cannot be negative")
    private Double actualFare; // Now taken from the user at request time

    @NotNull(message = "Distance is required at the time of request")
    @PositiveOrZero(message = "Distance cannot be negative")
    private Double distance; // New: Distance of the ride

    // We might add a requested_cab_type here if multiple cab types exist
}