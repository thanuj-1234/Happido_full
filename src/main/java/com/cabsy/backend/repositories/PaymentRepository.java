// src/main/java/com/cabsy/backend/repositories/PaymentRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRideId(Long rideId);
    Optional<Payment> findByTransactionId(String transactionId);
}