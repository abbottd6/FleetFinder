package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdateUserNotePrefDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.CustomNotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.PushSubscriptionService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user_notification_preferences")
@Slf4j
public class NotificationPrefsAndPushSubController {

    private final UserService userService;
    private final PushSubscriptionService pushSubService;
    private final CustomNotificationService cns;

    public NotificationPrefsAndPushSubController(UserService userService,
                                                 PushSubscriptionService pushSubService,
                                                 CustomNotificationService cns) {
        this.userService = userService;
        this.pushSubService = pushSubService;
        this.cns = cns;
    }

    @PutMapping("/update_discord_notification_pref")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> updateUserNotificationPreference(@AuthenticationPrincipal Jwt jwt,
                                                              @Valid @RequestBody UpdateUserNotePrefDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        Boolean response = this.userService.updateUserNotificationPreference(user, dto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_my_custom_notifications")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public Page<GetCustomNotificationResponseDto> getMyCustomNotifications(@AuthenticationPrincipal Jwt jwt,
                                                      @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return cns.getAllMyCustomNotifications(user, pageDto);
    }

    @PostMapping("/create_custom_notification")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> createCustomNotification(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody CreateOrEditCustomNotificationDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        GetCustomNotificationResponseDto customNote = cns.createNewCustomNotification(user, dto);

        return ResponseEntity.ok(customNote);
    }

    @PutMapping("/edit_custom_notification/{noteId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> editCustomNotification(@AuthenticationPrincipal Jwt jwt,
                                                    @PathVariable Long noteId,
                                                    @RequestBody CreateOrEditCustomNotificationDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        GetCustomNotificationResponseDto customNote = cns.editCustomNotification(user, noteId, dto);

        return ResponseEntity.ok(customNote);
    }

    @PatchMapping("/custom_notification_state_change/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> enablementStateChange(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Long id,
                                                   @RequestParam Boolean enabledState) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        cns.enablementStateChange(user, id, enabledState);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_custom_notification/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteCustomNotification(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable Long id) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        cns.deleteCustomNotification(user, id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/get_my_push_subs")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getMyPushSubs(@AuthenticationPrincipal Jwt jwt,
                                           @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Page<GetPushSubDto> responseDtos = pushSubService.getAllMyPushSubs(user, pageDto);

        Map<String, Page<GetPushSubDto>> response = new HashMap<>();
        response.put("response", responseDtos);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create_push_sub")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> createPushSub(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody CreatePushSubRequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        GetPushSubDto responseDto = pushSubService.createNewPushSub(user, dto);

        Map<String, GetPushSubDto> response = new HashMap<>();
        response.put("response", responseDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update_push_sub")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> updatePushSub(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody UpdatePushSubRequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        GetPushSubDto responseDto = pushSubService.updatePushSub(user, dto);

        Map<String, GetPushSubDto> response = new HashMap<>();
        response.put("response", responseDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete_push_sub/{subId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deletePushSub(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long subId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Integer deleted = pushSubService.deletePushSub(user, subId);

        Map<String, Integer> response = new HashMap<>();
        response.put("response", deleted);

        return ResponseEntity.ok(response);
    }
}
