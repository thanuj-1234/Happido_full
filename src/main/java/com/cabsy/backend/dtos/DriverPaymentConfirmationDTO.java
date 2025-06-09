// src/main/java/com/cabsy/backend/dtos/DriverPaymentConfirmationDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for carrying payment confirmation details from a driver.
 * This DTO represents the information a driver provides when confirming
 * a payment for a ride (e.g., cash payment received, or a payment processed
 * via a driver-side terminal).
 */
@Data
public class DriverPaymentConfirmationDTO {

    @NotNull(message = "Ride ID is mandatory")
    private Long rideId; // The ID of the ride for which the payment is being confirmed

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount; // The amount of the payment

    @NotBlank(message = "Payment method is mandatory")
    private String paymentMethod; // The method of payment (e.g., "Cash", "Card_Terminal")

    private String transactionId; // Optional: For cases where an external transaction ID is generated (e.g., card payments)
}
