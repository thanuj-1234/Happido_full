package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.RatingRequestDTO;
import com.cabsy.backend.dtos.RatingResponseDTO;
import com.cabsy.backend.services.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponseDTO>> addRating(@Valid @RequestBody RatingRequestDTO ratingRequestDTO) {
        try {
            RatingResponseDTO newRating = ratingService.addRating(
                    ratingRequestDTO.getRideId(),
                    ratingRequestDTO.getUserId(),
                    ratingRequestDTO.getDriverId(),
                    ratingRequestDTO.getStars(),
                    ratingRequestDTO.getComment() // Pass the comment/feedback
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Rating added successfully", newRating));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to add rating", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RatingResponseDTO>> getRatingById(@PathVariable Long id) {
        return ratingService.getRatingById(id)
                .map(ratingDTO -> ResponseEntity.ok(ApiResponse.success("Rating fetched successfully", ratingDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Rating not found", "Rating with ID " + id + " does not exist")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RatingResponseDTO>>> getRatingsByUserId(@PathVariable Long userId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Ratings for user fetched successfully", ratings));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<RatingResponseDTO>>> getRatingsByDriverId(@PathVariable Long driverId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByDriverId(driverId);
        return ResponseEntity.ok(ApiResponse.success("Ratings for driver fetched successfully", ratings));
    }
}