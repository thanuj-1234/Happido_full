package com.cabsy.backend.services;

import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Long rideId, Double amount, String method, Long driverId); // Updated method signature
    Optional<Payment> getPaymentById(Long paymentId);
    Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String transactionId);
    Optional<Payment> getPaymentByRideId(Long rideId);
    Payment savePayment(Payment payment);
}