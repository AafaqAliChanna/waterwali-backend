package com.waterwali.backend.entity;

// The lifecycle of one order, exactly as described in the handbook's request flow.
public enum OrderStatus {
    PENDING,      // just placed, waiting for a driver to accept (Phase 3)
    ACCEPTED,     // a driver accepted (Phase 3)
    IN_PROGRESS,  // driver is on the way / delivering (Phase 3)
    COMPLETED,    // water delivered, cash collected (triggers wallet deduction in Phase 4)
    CANCELLED
}
