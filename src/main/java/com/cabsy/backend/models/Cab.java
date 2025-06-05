// src/main/java/com/cabsy/backend/models/Cab.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List; // For one-to-many relationship with Ride

@Entity
@Table(name = "cabs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String licensePlate;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(nullable = false)
    private Integer capacity; // Number of passengers the cab can hold

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CabStatus status; // e.g., AVAILABLE, ON_TRIP, MAINTENANCE

    // One-to-one relationship with Driver
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true) // driver_id column in cabs table
    private Driver driver;

    // One-to-Many relationship with Ride (cab used for multiple rides)
    @OneToMany(mappedBy = "cab", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ride> rides;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = CabStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}