// src/main/java/com/cabsy/backend/models/Driver.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(nullable = false, length = 255) // This will store the HASHED password
    private String password; // No plaintext, store BCrypt hash

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DriverStatus status; // e.g., AVAILABLE, OCCUPIED, OFFLINE

    @Column(nullable = false)
    private Double rating; // Average rating for the driver

    // Removed: currentLocationLat and currentLocationLon

    // One-to-one relationship with Cab
    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Cab cab;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.rating == null) {
            this.rating = 0.0;
        }
        if (this.status == null) {
            this.status = DriverStatus.APPROVAL_PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}