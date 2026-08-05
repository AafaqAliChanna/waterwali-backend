package com.waterwali.backend.dto;

import com.waterwali.backend.entity.TankerSize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Tanker size is required")
    private TankerSize tankerSize;
}
