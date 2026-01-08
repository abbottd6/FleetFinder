package com.sc_fleetfinder.fleets.config;

import com.sc_fleetfinder.fleets.messaging.websocket.JwtQueryParamHandshakeInterceptor;
import com.sc_fleetfinder.fleets.messaging.websocket.JwtSubHandshakeHandler;
import com.sc_fleetfinder.fleets.messaging.websocket.StompJwtChannelInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@Profile("!test")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.ws.allowed-origins}")
    private String wsAppUrl;

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;
    private final JwtDecoder jwtDecoder;

    public WebSocketConfig(StompJwtChannelInterceptor stompJwtChannelInterceptor, JwtDecoder jwtDecoder) {
        this.stompJwtChannelInterceptor = stompJwtChannelInterceptor;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(256 * 1024);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompJwtChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/websocket")
                .setHandshakeHandler(new JwtSubHandshakeHandler(jwtDecoder))
                .addInterceptors(new JwtQueryParamHandshakeInterceptor())
                .setAllowedOriginPatterns(wsAppUrl);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //client -> server messages
        registry.setApplicationDestinationPrefixes("/app");

        //server -> client broadcasts
        registry.enableSimpleBroker("/topic", "/queue");

        //enables /user/queue/** routing
        registry.setUserDestinationPrefix("/user");
    }
}
