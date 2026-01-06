package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<GetNotificationDto> getMyNotifications(Users user, Pageable pageable);
    void deleteNotification(Users user, Long noteId);
    Integer deleteAllNotifications(Users user);
    void createAndSendDeleteNotification(ListingArchive archive,
                                         ModerationIssue issue,
                                         NotificationType type,
                                         ModListingAction action);
}
