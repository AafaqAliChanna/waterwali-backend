package com.waterwali.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

// One wallet per driver. This is the "current balance" — the ledger below
// is the permanent proof of HOW it got to that number.
@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID driverId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;
}