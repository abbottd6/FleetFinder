package com.sc_fleetfinder.fleets.messaging.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtSubHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtDecoder jwtDecoder;

    public JwtSubHandshakeHandler(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String token = (String) attributes.get("token");
        if(token == null) return super.determineUser(request, wsHandler, attributes);

        Jwt jwt = jwtDecoder.decode(token);
        String sub = jwt.getSubject();

        return () -> sub;
    }
}
