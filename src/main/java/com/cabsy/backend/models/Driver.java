// src/main/java/com/cabsy/backend/models/Driver.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;          // Generates getters, setters, equals, hashCode, and toString
import lombok.NoArgsConstructor;  // Generates a no-argument constructor
import lombok.AllArgsConstructor; // Generates a constructor with all fields as arguments
import java.time.LocalDateTime; // For tracking creation and update timestamps

/**
 * Represents a Driver entity in the system, mapping to the 'drivers' table in the database.
 * Includes fields for personal details, authentication, operational status, and rating.
 */
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

    @Column(nullable = false, length = 255) // This will store the HASHED password (e.g., BCrypt hash)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Stores the enum name (e.g., "AVAILABLE") as a string in the DB
    private DriverStatus status;

    @Column(nullable = false)
    private Double rating; // Average rating for the driver, e.g., 0.0 to 5.0

    // One-to-one relationship with Cab: A driver can be associated with one cab
    // 'mappedBy' indicates that the Driver entity is the inverse side of the relationship
    // and the 'driver' field in the Cab entity is the owner.
    // 'cascade = CascadeType.ALL' ensures all operations (persist, merge, remove) cascade to the Cab entity.
    // 'fetch = FetchType.LAZY' means the Cab will only be loaded when explicitly accessed.
    // 'orphanRemoval = true' means if a Cab is disassociated from a Driver, it will be removed.
    

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Callback method executed before the entity is persisted (inserted) into the database.
     * Sets creation and update timestamps, and default values for rating and status.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.rating == null) {
            this.rating = 0.0; // Default rating for a new driver
        }
        if (this.status == null) {
            this.status = DriverStatus.APPROVAL_PENDING; // Default status for a new driver
        }
    }

    /**
     * Callback method executed before the entity is updated in the database.
     * Updates the 'updatedAt' timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
