package com.waterwali.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// A permanent receipt. NEVER updated or deleted after creation --
// this is what lets us prove, forever, exactly where every rupee went.
@Entity
@Table(name = "wallet_ledger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletLedger {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    private UUID orderId; // null for a manual top-up, set for a commission deduction

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // negative = money left the wallet, positive = money added

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerType type;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}