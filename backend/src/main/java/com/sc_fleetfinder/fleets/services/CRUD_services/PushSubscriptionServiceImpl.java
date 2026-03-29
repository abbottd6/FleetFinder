package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.UnsuccessfulPushSubscriptionException;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationService;
import com.sc_fleetfinder.fleets.utils.ExternalNotifcationResult;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionRepository pushSubRepo;
    private final ModelMapper modelMapper;
    private final PushNotificationService pushNotificationService;

    public PushSubscriptionServiceImpl(PushSubscriptionRepository pushSubRepo,
                                       @Qualifier("pushSubscriptionMapper")ModelMapper modelMapper,
                                       PushNotificationService pushNotificationService) {
        this.pushSubRepo = pushSubRepo;
        this.modelMapper = modelMapper;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public Page<GetPushSubDto> getAllMyPushSubs(Users user, GenericPageRequestDto pageDto) {
        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        Page<PushSubscription> entityPage = pushSubRepo.getPageOfPushSubscriptionsByUser(user, pageable);
        return entityPage.map(pushSub -> modelMapper.map(pushSub, GetPushSubDto.class));
    }

    @Override
    @Transactional
    public GetPushSubDto createNewPushSub(Users user, CreatePushSubRequestDto dto) {

        if(pushSubRepo.findByUserAndDeviceUrl(user, dto.getDeviceUrl()).isPresent()) {
            throw new DuplicateEntryException("Push subscription already exists for this user and device.");
        }

        PushSubscription newPushSub = new PushSubscription(user, dto);

        PushSubscription saved = pushSubRepo.save(newPushSub);

        String testPayload = "Your push subscription '" + saved.getUserLabel() + "' was created successfully ";
        Map<ExternalNotifcationResult, HttpStatus> testResult = new HashMap<>();
        try {
            testResult = pushNotificationService.sendPushNotification(saved, testPayload);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        if(testResult.containsKey(ExternalNotifcationResult.FAILURE)) {
            throw new UnsuccessfulPushSubscriptionException("The test message returned a failure response.");
        }

        return modelMapper.map(saved, GetPushSubDto.class);
    }

    @Override
    @Transactional
    public GetPushSubDto updatePushSub(Users user, UpdatePushSubRequestDto dto) {

        PushSubscription toUpdate = pushSubRepo.findByUserAndIdPushSub(user, dto.getIdPushSub())
                .orElseThrow(() -> new ActionNotAuthorizedException(user.getUserId(), "update", "PushSubscription",
                        dto.getIdPushSub()));

        switch (dto.getLabel()) {
            case "sysNotes":
                toUpdate.setSysNotesEnabled(dto.getValue());
                break;
            case "groupNotes":
                toUpdate.setGroupNotesEnabled(dto.getValue());
                break;
            case "socialNotes":
                toUpdate.setSocialNotesEnabled(dto.getValue());
                break;
            default:
                log.error("A user attempted to update a PushSubscription with a label that does not match one of the " +
                        "explicitly defined types: {}. Should be 'sysNotes', 'groupNotes', or 'socialNotes'."
                        , dto.getLabel());

                throw new IllegalArgumentException(dto.getLabel() + " is not a valid notification category.");
        }

        PushSubscription saved = pushSubRepo.save(toUpdate);

        return modelMapper.map(saved, GetPushSubDto.class);
    }

    @Override
    @Transactional
    public Integer deletePushSub(Users user, Long idPushSub) {
        Integer deletedCount = pushSubRepo.deleteByUserAndIdPushSub(user, idPushSub);

        if(deletedCount == 0) {
            log.warn("User with ID: {} attempted to delete a PushSubscription with ID: {}. \n"
                    + "The action failed because this PushSub either did not belong to them, "
                    + " or it did not exist.", user.getUserId(), idPushSub);

            throw new ResourceNotFoundException("PushSubscription", user.getUserId(),
                    idPushSub);
        }
        else {
            return deletedCount;
        }
    }
}
