package com.waterwali.backend.controller;

import com.waterwali.backend.dto.LocationUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// Not a normal @RestController -- this handles WebSocket messages, not HTTP requests.
@Controller
public class LocationController {

    private final SimpMessagingTemplate messagingTemplate;

    public LocationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Driver app sends to: /app/location/update
    // This immediately re-broadcasts it to: /topic/order/{orderId}/location
    // Any customer app "listening" on that exact topic instantly receives it.
    @MessageMapping("/location/update")
    public void updateLocation(LocationUpdate update) {
        String destination = "/topic/order/" + update.getOrderId() + "/location";
        messagingTemplate.convertAndSend(destination, update);
    }
}