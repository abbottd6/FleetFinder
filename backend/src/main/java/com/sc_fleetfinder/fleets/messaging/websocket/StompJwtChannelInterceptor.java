package com.sc_fleetfinder.fleets.messaging.websocket;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public StompJwtChannelInterceptor(final JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor == null) return message;


        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = firstNativeHeader(accessor, "Authorization");
            if (!StringUtils.hasText(authHeader)) {
                authHeader = firstNativeHeader(accessor, "X-Authorization");
            }

            if (!StringUtils.hasText(authHeader) && !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Message is missing Bearer token.");
            }

            String token = authHeader.substring("Bearer ".length());

            Jwt jwt = jwtDecoder.decode(token);

            String principalName = jwt.getSubject();

            AbstractAuthenticationToken authToken = new JwtAuthenticationToken(jwt, List.of(), principalName);

            accessor.setUser(authToken);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private static String firstNativeHeader(StompHeaderAccessor accessor, String headerName) {
        List<String> values = accessor.getNativeHeader(headerName);
        if ((values != null && !values.isEmpty())) {
            return values.getFirst();
        } else {
            return null;
        }
    }
}
