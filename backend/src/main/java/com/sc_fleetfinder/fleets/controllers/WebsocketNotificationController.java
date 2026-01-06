package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebsocketNotificationController {

    private final NotificationService notificationService;

    public WebsocketNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
