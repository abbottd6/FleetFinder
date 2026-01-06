package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notify")
@PreAuthorize("isAuthenticated() and hasRole('user')")
public class NotificationController {

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @PostMapping("/my_notifications")
    public Page<GetNotificationDto> getMyNotifications(@AuthenticationPrincipal Jwt jwt,
                                                       GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return notificationService.getMyNotifications(user, pageable);
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<?> deleteNotification(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long noteId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        notificationService.deleteNotification(user, noteId);
        return ResponseEntity.ok(Map.of("deleted", true, "noteId", noteId));
    }

    @DeleteMapping("/delete_all")
    public ResponseEntity<?> deleteAllMyNotifications(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Integer count = notificationService.deleteAllNotifications(user);

        return ResponseEntity.ok(Map.of("deleted", true, "count", count));
    }
}
