package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<GetNotificationDto> getMyDropdownNotifications(Users user, Pageable pageable);
    Page<GetNotificationDto> getAllMyNotifications(Users user, Pageable pageable);
    void removeDropdownPriority(Users user, Long notificationId);
    void deleteNotification(Users user, Long noteId);
    Integer deleteAllNotifications(Users user);
    void generateOutboxNotificationForModAction(ModListingAction action);
    Integer updateRead(Users user, ReceiveReadNotesDto dto);
    Integer countUnread(Long userId);
    void prepareAndSendOutboxNotification(NotificationOutbox single);
}
