// src/main/java/com/cabsy/backend/models/Payment.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", unique = true, nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    @Column(nullable = false, length = 50)
    private String paymentMethod; // e.g., "Credit Card", "Cash", "Wallet"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., PENDING, COMPLETED, FAILED

    @Column(name = "transaction_id", length = 255)
    private String transactionId; // ID from payment gateway

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.paymentTime == null) {
            this.paymentTime = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}