package com.cabsy.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequestDTO {

    @NotNull(message = "Ride ID is required")
    private Long rideId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Stars are required")
    @Min(value = 1, message = "Stars must be at least 1")
    @Max(value = 5, message = "Stars must be at most 5")
    private Integer stars;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment; // This is where the feedback will be received
}