// src/main/java/com/cabsy/backend/models/CabStatus.java
package com.cabsy.backend.models;

public enum CabStatus {
    AVAILABLE,    // Ready to be assigned to a driver or waiting for a ride
    ON_TRIP,      // Currently on a ride
    MAINTENANCE,  // Out of service for maintenance
    DISABLED      // Permanently disabled
}