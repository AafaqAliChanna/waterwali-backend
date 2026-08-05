package com.waterwali.backend.dto;

import com.waterwali.backend.entity.TankerSize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// What the Flutter app sends when the customer drops a pin and picks a size.
// Notice: NO price field here. The customer can never set their own price --
// that is calculated server-side in OrderService, from tankerSize only.
@Data
public class CreateOrderRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Tanker size is required")
    private TankerSize tankerSize;
}
