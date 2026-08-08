
package com.waterwali.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

// Sets up ONE open "phone line" endpoint (/ws) that both driver and
// customer apps connect to, using the STOMP messaging protocol on top of WebSockets.
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Apps connect here first to "pick up the phone."
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages sent to "/topic/..." get broadcast to everyone listening on that topic.
        registry.enableSimpleBroker("/topic");
        // Messages the driver SENDS start with "/app/..." (see LocationController below).
        registry.setApplicationDestinationPrefixes("/app");
    }
}