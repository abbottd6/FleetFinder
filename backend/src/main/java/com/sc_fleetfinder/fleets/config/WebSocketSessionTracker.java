package com.sc_fleetfinder.fleets.config;

import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSessionTracker implements ApplicationListener<AbstractSubProtocolEvent> {

    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    @Override
    public void onApplicationEvent(AbstractSubProtocolEvent event) {
        if(!(event instanceof SessionConnectedEvent || event instanceof SessionDisconnectEvent)) {
            return;
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();

        assert principal != null;

        String kcId = principal.getName();

        if(kcId.isBlank()) {
            log.debug("WS tracker had a blank userId");
            return;
        }

        if (event instanceof SessionConnectedEvent) {
            log.debug("connected: {}", kcId);
            connectedUsers.add(principal.getName());
        } else {
            log.debug("disconnected: {}", kcId);
            connectedUsers.remove(principal.getName());
        }
    }

    public boolean isUserConnected(String kcId) {
        return connectedUsers.contains(kcId);
    }
}
