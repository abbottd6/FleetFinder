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
//@Component
@RequiredArgsConstructor
// Not in use. Retained for potential future use on connectivity tracking, but the use case
// I added this for now uses UserActivityCache and last_active users field instead
public class WebSocketSessionTracker implements ApplicationListener<AbstractSubProtocolEvent> {

    private final UserService userService;
    private final Set<Long> connectedUsers = ConcurrentHashMap.newKeySet();

    @Override
    public void onApplicationEvent(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();

        assert principal != null;
        Long userId = userService.verifyUser(principal.getName()).getUserId();

        if(userId == null) {
            log.warn("user null");
            return;
        }

        if (event instanceof SessionConnectedEvent) {
            log.warn("connected: {}", userId);
            connectedUsers.add(userId);
        } else if (event instanceof SessionDisconnectEvent) {
            log.warn("disconnected: {}", userId);
            connectedUsers.remove(userId);
        }
    }

    public boolean isUserConnected(Long userId) {
        return connectedUsers.contains(userId);
    }
}
