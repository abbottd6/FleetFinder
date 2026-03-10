package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationUnreadCountDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class WebsocketNotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public WebsocketNotificationController(NotificationService notificationService,
                                           SimpMessagingTemplate messagingTemplate,
                                           UserService userService) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/system.notify/receive_read")
    public void updateRead(Principal principal, @Payload ReceiveReadNotesDto readDto) {
        String userSub = principal.getName();
        Users user = userService.verifyUser(userSub);

        Integer markedCount = notificationService.updateRead(user, readDto);

        if(markedCount != null && markedCount > 0) {
            Integer count = notificationService.countUnread(user.getUserId());
            NotificationUnreadCountDto dto = new NotificationUnreadCountDto(count);

            messagingTemplate.convertAndSendToUser(
                    userSub,
                    "/queue/system.notify_count",
                    dto
            );
        }
    }

    @MessageMapping("/system.notify/get_unread")
    public void getUnreadCount(Principal principal) {
        String userSub = principal.getName();
        Users user = userService.verifyUser(userSub);

        Integer count = notificationService.countUnread(user.getUserId());
        NotificationUnreadCountDto dto = new NotificationUnreadCountDto(count);



        messagingTemplate.convertAndSendToUser(
                userSub,
                "queue/system.notify_count",
                dto
        );
    }
}
