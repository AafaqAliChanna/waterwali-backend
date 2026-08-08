package com.waterwali.backend.entity;

public enum LedgerType {
    COMMISSION,  // money leaving the wallet (7% cut per completed order)
    TOPUP        // money entering the wallet (driver adds funds)
}