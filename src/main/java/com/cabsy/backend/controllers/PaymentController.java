package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.PaymentRequestDTO;
import com.cabsy.backend.dtos.PaymentResponseDTO;
import com.cabsy.backend.mappers.PaymentMapper;
import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus;
import com.cabsy.backend.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Endpoint for drivers to record a payment after a ride is completed.
     * This method will create a new payment record or update an existing PENDING one.
     *
     * @param paymentRequestDTO DTO containing rideId, amount, paymentMethod, and optionally transactionId
     * @return ResponseEntity with ApiResponse indicating success or failure
     */
    @PostMapping("/record")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> recordPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            // Check if a payment for this ride already exists (e.g., if it was initiated as PENDING by the system)
            Optional<Payment> existingPayment = paymentService.getPaymentByRideId(paymentRequestDTO.getRideId());

            Payment payment;
            if (existingPayment.isPresent()) {
                // If a PENDING payment exists, update its status and details
                payment = paymentService.updatePaymentStatus(
                        existingPayment.get().getId(),
                        PaymentStatus.COMPLETED, // Assume driver recording means payment is completed
                        paymentRequestDTO.getTransactionId()
                );
                payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod()); // Update method if needed
                payment = paymentService.savePayment(payment); // Save the updated payment
            } else {
                // Otherwise, create a new payment record (for cash payments, etc.)
                payment = paymentService.createPayment(
                        paymentRequestDTO.getRideId(),
                        paymentRequestDTO.getAmount(),
                        paymentRequestDTO.getPaymentMethod()
                );
                // Mark as completed immediately if driver is recording it directly
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(paymentRequestDTO.getTransactionId());
                payment = paymentService.savePayment(payment);
            }

            PaymentResponseDTO responseDTO = paymentMapper.toDto(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to record payment", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(paymentMapper::toDto)
                .map(paymentDTO -> ResponseEntity.ok(ApiResponse.success("Payment fetched successfully", paymentDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payment not found", "Payment with ID " + id + " does not exist")));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByRideId(@PathVariable Long rideId) {
        return paymentService.getPaymentByRideId(rideId)
                .map(paymentMapper::toDto)
                .map(paymentDTO -> ResponseEntity.ok(ApiResponse.success("Payment fetched successfully for ride", paymentDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payment not found", "Payment for ride with ID " + rideId + " does not exist")));
    }

    // You can add more endpoints here for admin functionalities, e.g., listing all payments
}