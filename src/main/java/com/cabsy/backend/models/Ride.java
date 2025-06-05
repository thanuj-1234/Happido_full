// src/main/java/com/cabsy/backend/models/Ride.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id") // Can be null if not yet assigned
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cab_id") // Can be null if not yet assigned
    private Cab cab;

    @Column(nullable = false)
    private Double pickupLat;

    @Column(nullable = false)
    private Double pickupLon;

    @Column(nullable = false)
    private Double destinationLat;

    @Column(nullable = false)
    private Double destinationLon;

    @Column(length = 255)
    private String pickupAddress; // Optional: Store formatted address

    @Column(length = 255)
    private String destinationAddress; // Optional: Store formatted address

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(nullable = false)
    private Double estimatedFare;

    @Column
    private Double actualFare; // Set after completion

    @Column(nullable = false)
    private LocalDateTime requestTime;

    @Column
    private LocalDateTime startTime; // When ride begins

    @Column
    private LocalDateTime endTime; // When ride ends

    // One-to-one relationship with Payment
    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Payment payment;

    // One-to-one relationship with Rating
    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Rating rating;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }
        if (this.requestTime == null) {
            this.requestTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}