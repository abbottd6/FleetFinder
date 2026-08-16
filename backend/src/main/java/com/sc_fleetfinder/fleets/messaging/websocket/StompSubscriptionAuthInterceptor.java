package com.sc_fleetfinder.fleets.messaging.websocket;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StompSubscriptionAuthInterceptor implements ChannelInterceptor {

    private static final Pattern CHAT_TOPIC = Pattern.compile("^/topic/chat/(\\d+)$");

    private final GroupMemberUserService groupMemberUserService;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }

        String destination = accessor.getDestination();
        if (destination == null) return message;

        Matcher m = CHAT_TOPIC.matcher(destination);
        if (m.matches()) {
            Principal principal = accessor.getUser();
            if(principal == null) {
                throw new AccessDeniedException("User is not authorized to perform this action");
            }

            Users user = userService.verifyUser(principal.getName());

            Long groupId = Long.parseLong(m.group(1));
            try {
                GroupMember member = groupMemberUserService.verifyAndReturnUserAsGroupMember(user, groupId);
            } catch (ResourceNotFoundException e) {
                throw new MessagingException("Not a member of group " + groupId);
            }
        }
        return message;
    }
}
