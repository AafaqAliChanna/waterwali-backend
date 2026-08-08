package com.waterwali.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletResponse {
    private BigDecimal balance;
    private boolean isOnline;
}