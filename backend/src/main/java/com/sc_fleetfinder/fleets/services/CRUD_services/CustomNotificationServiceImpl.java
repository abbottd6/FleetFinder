package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.UserCustomNotificationRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPageOfCustomNotificationsAndEnabledCount;
import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ContentLimitException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomNotificationServiceImpl implements CustomNotificationService {

    private final int ENABLED_LIMIT = 5;
    private final UserCustomNotificationRepository cnr;
    private final ModelMapper modelMapper;

    public CustomNotificationServiceImpl(UserCustomNotificationRepository cnr,
                                         ModelMapper modelMapper) {
        this.cnr = cnr;
        this.modelMapper = modelMapper;
    }

    @Override
    public GetPageOfCustomNotificationsAndEnabledCount getAllMyCustomNotifications(Users user, GenericPageRequestDto pageDto) {
        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        Page<UserCustomNotification> entityPage = cnr.findByUser(user, pageable);

        Page<GetCustomNotificationResponseDto> dtoPage = entityPage.map(cNote ->
                modelMapper.map(cNote, GetCustomNotificationResponseDto.class));

        Integer enabledCount = cnr.countEnabledByUser(user.getUserId());

        return new GetPageOfCustomNotificationsAndEnabledCount(dtoPage, enabledCount);
    }

    @Override
    public GetCustomNotificationResponseDto createNewCustomNotification(Users user, CreateOrEditCustomNotificationDto dto) {
        int TOTAL_LIMIT = 10;

        Integer existingCount = cnr.countByUser(user);
        Integer enabledCount = cnr.countEnabledByUser(user.getUserId());

        if(existingCount >= TOTAL_LIMIT) {
            throw new ContentLimitException(user.getUserId(), "UserCustomNotification", TOTAL_LIMIT);
        }

        UserCustomNotification newCustom = modelMapper.map(dto, UserCustomNotification.class);
        newCustom.setUser(user);

        if(enabledCount >= ENABLED_LIMIT) {
            newCustom.setEnabled(false);
        }

        UserCustomNotification saved = cnr.save(newCustom);

        return modelMapper.map(saved, GetCustomNotificationResponseDto.class);
    }

    @Override
    public GetCustomNotificationResponseDto editCustomNotification(Users user, Long noteId, CreateOrEditCustomNotificationDto dto) {
        UserCustomNotification toEdit = cnr.findByUserAndCustomNoteId(user, noteId)
                .orElseThrow(() -> new ActionNotAuthorizedException(user.getUserId(), "edit", "UserCustomNotification",
                        noteId));

        modelMapper.map(dto, toEdit);

        UserCustomNotification saved = cnr.save(toEdit);

        return modelMapper.map(saved, GetCustomNotificationResponseDto.class);
    }

    @Override
    public GetCustomNotificationResponseDto enablementStateChange(Users user, Long customNoteId, Boolean state) {
        UserCustomNotification toChange = cnr.findByUserAndCustomNoteId(user, customNoteId)
                .orElseThrow(() -> new ActionNotAuthorizedException(user.getUserId(), "state change",
                        "UserCustomNotification", customNoteId));

        Integer enabledCount = cnr.countEnabledByUser(user.getUserId());

        //noinspection PointlessBooleanExpression is not pointless: clarifying
        if((state == true) && (enabledCount >= ENABLED_LIMIT)) {
            throw new ContentLimitException(user.getUserId(), "UserCustomNotification", ENABLED_LIMIT);
        }


        toChange.setEnabled(state);

        cnr.save(toChange);
        cnr.flush();

        return modelMapper.map(toChange, GetCustomNotificationResponseDto.class);
    }

    @Override
    public void deleteCustomNotification(Users user, Long customNoteId) {
        UserCustomNotification toDelete = cnr.findByUserAndCustomNoteId(user, customNoteId)
                .orElseThrow(() -> new ActionNotAuthorizedException(user.getUserId(), "delete",
                        "UserCustomNotification", customNoteId));

        cnr.delete(toDelete);
        cnr.flush();
    }
}
