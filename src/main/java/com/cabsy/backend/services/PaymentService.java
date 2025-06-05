// src/main/java/com/cabsy/backend/services/PaymentService.java
package com.cabsy.backend.services;

import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Long rideId, Double amount, String method);
    Optional<Payment> getPaymentById(Long paymentId);
    Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String transactionId);
    // Add methods for processing payment (integration with payment gateway)
}