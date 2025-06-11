package com.cabsy.backend.mappers;

import com.cabsy.backend.dtos.PaymentResponseDTO;
import com.cabsy.backend.models.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDTO toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setRideId(payment.getRide() != null ? payment.getRide().getId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentTime(payment.getPaymentTime());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        dto.setDriverId(payment.getDriverId()); // New mapping
        return dto;
    }
}