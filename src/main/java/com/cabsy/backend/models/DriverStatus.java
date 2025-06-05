// src/main/java/com/cabsy/backend/models/DriverStatus.java
package com.cabsy.backend.models;

public enum DriverStatus {
    AVAILABLE,           // Ready to accept rides
    OCCUPIED,            // Currently on a ride
    OFFLINE,             // Logged out or not accepting rides
    APPROVAL_PENDING,    // Newly registered driver awaiting admin approval
    BLOCKED              // Driver account is blocked
}