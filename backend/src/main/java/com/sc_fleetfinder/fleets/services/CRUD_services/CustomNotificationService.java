package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface CustomNotificationService {

    Page<GetCustomNotificationResponseDto> getAllMyCustomNotifications(Users user, GenericPageRequestDto pageDto);

    GetCustomNotificationResponseDto createNewCustomNotification(Users user, CreateOrEditCustomNotificationDto dto);

    GetCustomNotificationResponseDto editCustomNotification(Users user, Long noteId, CreateOrEditCustomNotificationDto dto);

    void enablementStateChange(Users user, Long customNoteId, Boolean state);

    void deleteCustomNotification(Users user, Long customNoteId);
}
