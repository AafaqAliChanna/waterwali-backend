package com.waterwali.backend.entity;

// The choices a customer picks from when placing an order.
// Prices are kept server-side ONLY -- never trust a price sent from the app.
public enum TankerSize {
    SIZE_1000L,
    SIZE_2000L,
    SIZE_3000L,
    SIZE_5000L
}
