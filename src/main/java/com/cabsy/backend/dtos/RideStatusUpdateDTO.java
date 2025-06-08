// src/main/java/com/cabsy/backend/dtos/RideStatusUpdateDTO.java
package com.cabsy.backend.dtos;

import com.cabsy.backend.models.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideStatusUpdateDTO {

    @NotNull(message = "New ride status is required")
    private RideStatus status;
    private Long driverId; // Optional: Driver ID can be passed when updating status
}