
package com.waterwali.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdate {
    private UUID orderId;
    private double latitude;
    private double longitude;
}