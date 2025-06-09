package com.cabsy.backend.services.impl;

import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus;
import com.cabsy.backend.models.Ride; // Assuming Ride model exists
import com.cabsy.backend.repositories.PaymentRepository;
import com.cabsy.backend.repositories.RideRepository; // Assuming RideRepository exists
import com.cabsy.backend.services.PaymentService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository; // Inject RideRepository

    public PaymentServiceImpl(PaymentRepository paymentRepository, RideRepository rideRepository) {
        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
    }

    @Override
    @Transactional
    public Payment createPayment(Long rideId, Double amount, String method) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId)); // TODO: Custom exception

        // Check if a payment already exists for this ride to prevent duplicates
        if (paymentRepository.findByRideId(rideId).isPresent()) {
            throw new RuntimeException("Payment already exists for ride with id: " + rideId); // TODO: Custom exception
        }

        Payment payment = new Payment();
        payment.setRide(ride);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.PENDING); // Default status
        payment.setPaymentTime(LocalDateTime.now()); // Set initial payment time, can be updated later

        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String transactionId) {
        return paymentRepository.findById(paymentId).map(payment -> {
            payment.setStatus(newStatus);
            if (transactionId != null && !transactionId.isEmpty()) {
                payment.setTransactionId(transactionId);
            }
            // Optionally update payment time if status changes from PENDING to COMPLETED
            if (newStatus == PaymentStatus.COMPLETED && payment.getPaymentTime() == null) {
                payment.setPaymentTime(LocalDateTime.now());
            }
            return paymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId)); // TODO: Custom exception
    }

    @Override
    public Optional<Payment> getPaymentByRideId(Long rideId) {
        return paymentRepository.findByRideId(rideId);
    }

    @Override
    @Transactional
    public Payment savePayment(Payment payment) {
        // This method is useful for updating fields on an existing Payment object
        // that are not covered by updatePaymentStatus, like payment method.
        return paymentRepository.save(payment);
    }
}