package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notify")
@PreAuthorize("isAuthenticated() and hasRole('user')")
public class NotificationController {

    private final UserService userService;

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PostMapping("/my_dropdown_notifications")
    public Page<GetNotificationDto> getMyDropdownNotifications(@AuthenticationPrincipal Jwt jwt,
                                                       @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return notificationService.getMyDropdownNotifications(user, pageable);
    }

    @PostMapping("/all_my_notifications")
    public Page<GetNotificationDto> getAllMyNotifications(@AuthenticationPrincipal Jwt jwt,
                                                          @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return notificationService.getAllMyNotifications(user, pageable);
    }

    @DeleteMapping("/dropdown_remove/{noteId}")
    public ResponseEntity<?> removeNotificationDropdownPriority(@AuthenticationPrincipal Jwt jwt,
                                                                @PathVariable Long noteId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        notificationService.removeDropdownPriority(user, noteId);

        return ResponseEntity.ok().build();
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
