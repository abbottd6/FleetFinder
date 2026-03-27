package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface PushSubscriptionService {

    Page<GetPushSubDto> getAllMyPushSubs(Users user, GenericPageRequestDto pageDto);

    GetPushSubDto createNewPushSub(Users user, CreatePushSubRequestDto dto);

    GetPushSubDto updatePushSub(Users user, UpdatePushSubRequestDto dto);

    Integer deletePushSub(Users user, Long idPushSub);
}
