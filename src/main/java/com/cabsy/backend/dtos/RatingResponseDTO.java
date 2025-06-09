package com.cabsy.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private Long id;
    private Long rideId;
    private Long userId;
    private Long driverId;
    private Integer stars;
    private String comment;
    private LocalDateTime ratingTime;
}