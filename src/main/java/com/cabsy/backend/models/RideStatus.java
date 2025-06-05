// src/main/java/com/cabsy/backend/models/RideStatus.java
package com.cabsy.backend.models;

public enum RideStatus {
    REQUESTED,       // User has requested a ride
    ACCEPTED,        // Driver has accepted the ride
    ON_ROUTE_TO_PICKUP, // Driver is going to pickup location
    ARRIVED_AT_PICKUP,  // Driver is at pickup location
    IN_PROGRESS,     // Ride has started (user picked up)
    COMPLETED,       // Ride finished, user dropped off
    CANCELLED,       // Ride was cancelled
    NO_DRIVER_FOUND  // No driver could be assigned
}