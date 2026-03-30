package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.UnsuccessfulPushSubscriptionException;
import com.sc_fleetfinder.fleets.utils.ExternalNotifcationResult;
import jakarta.ws.rs.InternalServerErrorException;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.PushSubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PushSubscriptionServiceImplTest {

    @Mock
    private PushSubscriptionRepository pushSubRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private PushSubscriptionServiceImpl pushSubscriptionService;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
    }

    // ─── getAllMyPushSubs ──────────────────────────────────────────────────────

    @Test
    void getAllMyPushSubs_Success_ReturnsPage() {
        PushSubscription entity = new PushSubscription();
        GetPushSubDto dto = new GetPushSubDto();
        dto.setIdPushSub(1L);
        Page<PushSubscription> entityPage = new PageImpl<>(List.of(entity));

        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        when(pushSubRepository.getPageOfPushSubscriptionsByUser(eq(mockUser), any())).thenReturn(entityPage);
        when(modelMapper.map(entity, GetPushSubDto.class)).thenReturn(dto);

        Page<GetPushSubDto> result = pushSubscriptionService.getAllMyPushSubs(mockUser, pageDto);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getIdPushSub()).isEqualTo(1L);
    }

    @Test
    void getAllMyPushSubs_EmptyPage_ReturnsEmptyPage() {
        Page<PushSubscription> emptyPage = new PageImpl<>(List.of());
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        when(pushSubRepository.getPageOfPushSubscriptionsByUser(eq(mockUser), any())).thenReturn(emptyPage);

        Page<GetPushSubDto> result = pushSubscriptionService.getAllMyPushSubs(mockUser, pageDto);

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ─── createNewPushSub ─────────────────────────────────────────────────────

    @Test
    void createNewPushSub_Success() throws Exception {
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setUserLabel("My Device");
        dto.setDeviceUrl("https://push.example.com/sub/123");
        dto.setPublicKey("pubKey");
        dto.setBrowserSecret("secret");

        PushSubscription saved = new PushSubscription();
        saved.setUserLabel("My Device");
        GetPushSubDto responseDto = new GetPushSubDto();
        responseDto.setIdPushSub(1L);

        when(pushSubRepository.findByUserAndDeviceUrl(mockUser, dto.getDeviceUrl()))
                .thenReturn(Optional.empty());
        when(pushSubRepository.save(any(PushSubscription.class))).thenReturn(saved);
        when(pushNotificationService.sendPushNotification(eq(saved), any(String.class)))
                .thenReturn(Map.of(ExternalNotifcationResult.SUCCESS, org.springframework.http.HttpStatus.CREATED));
        when(modelMapper.map(saved, GetPushSubDto.class)).thenReturn(responseDto);

        GetPushSubDto result = pushSubscriptionService.createNewPushSub(mockUser, dto);

        assertThat(result.getIdPushSub()).isEqualTo(1L);
        verify(pushSubRepository).save(any(PushSubscription.class));
        verify(pushNotificationService).sendPushNotification(eq(saved), any(String.class));
    }

    @Test
    void createNewPushSub_PushServiceThrowsException_ThrowsInternalServerError() throws Exception {
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setDeviceUrl("https://push.example.com/sub/123");

        PushSubscription saved = new PushSubscription();

        when(pushSubRepository.findByUserAndDeviceUrl(mockUser, dto.getDeviceUrl()))
                .thenReturn(Optional.empty());
        when(pushSubRepository.save(any(PushSubscription.class))).thenReturn(saved);
        when(pushNotificationService.sendPushNotification(any(), any()))
                .thenThrow(new Exception("push service error"));

        assertThatThrownBy(() -> pushSubscriptionService.createNewPushSub(mockUser, dto))
                .isInstanceOf(InternalServerErrorException.class);
    }

    @Test
    void createNewPushSub_PushServiceReturnsFailure_ThrowsUnsuccessfulPushSubscriptionException() throws Exception {
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setDeviceUrl("https://push.example.com/sub/123");

        PushSubscription saved = new PushSubscription();

        when(pushSubRepository.findByUserAndDeviceUrl(mockUser, dto.getDeviceUrl()))
                .thenReturn(Optional.empty());
        when(pushSubRepository.save(any(PushSubscription.class))).thenReturn(saved);
        when(pushNotificationService.sendPushNotification(any(), any()))
                .thenReturn(Map.of(ExternalNotifcationResult.FAILURE, org.springframework.http.HttpStatus.GONE));

        assertThatThrownBy(() -> pushSubscriptionService.createNewPushSub(mockUser, dto))
                .isInstanceOf(UnsuccessfulPushSubscriptionException.class);
    }

    @Test
    void createNewPushSub_DuplicateDevice_ThrowsIllegalArgumentException() {
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setDeviceUrl("https://push.example.com/sub/123");

        when(pushSubRepository.findByUserAndDeviceUrl(mockUser, dto.getDeviceUrl()))
                .thenReturn(Optional.of(new PushSubscription()));

        assertThatThrownBy(() -> pushSubscriptionService.createNewPushSub(mockUser, dto))
                .isInstanceOf(DuplicateEntryException.class);
    }

    // ─── updatePushSub ────────────────────────────────────────────────────────

    @Test
    void updatePushSub_Success_SysNotes() {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("sysNotes");
        dto.setValue(true);

        PushSubscription entity = new PushSubscription();
        entity.setSysNotesEnabled(false);

        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(pushSubRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, GetPushSubDto.class)).thenReturn(new GetPushSubDto());

        pushSubscriptionService.updatePushSub(mockUser, dto);

        assertThat(entity.getSysNotesEnabled()).isTrue();
        verify(pushSubRepository).save(entity);
    }

    @Test
    void updatePushSub_Success_GroupNotes() {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("groupNotes");
        dto.setValue(false);

        PushSubscription entity = new PushSubscription();
        entity.setGroupNotesEnabled(true);

        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(pushSubRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, GetPushSubDto.class)).thenReturn(new GetPushSubDto());

        pushSubscriptionService.updatePushSub(mockUser, dto);

        assertThat(entity.getGroupNotesEnabled()).isFalse();
        verify(pushSubRepository).save(entity);
    }

    @Test
    void updatePushSub_Success_SocialNotes() {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("socialNotes");
        dto.setValue(false);

        PushSubscription entity = new PushSubscription();
        entity.setSocialNotesEnabled(true);

        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(pushSubRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, GetPushSubDto.class)).thenReturn(new GetPushSubDto());

        pushSubscriptionService.updatePushSub(mockUser, dto);

        assertThat(entity.getSocialNotesEnabled()).isFalse();
        verify(pushSubRepository).save(entity);
    }

    @Test
    void updatePushSub_NotFound_ThrowsActionNotAuthorizedException() {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(99L);
        dto.setLabel("sysNotes");
        dto.setValue(true);

        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pushSubscriptionService.updatePushSub(mockUser, dto))
                .isInstanceOf(ActionNotAuthorizedException.class);
    }

    @Test
    void updatePushSub_WrongUser_ThrowsActionNotAuthorizedException() {
        // Ownership enforced by repo query: findByUserAndIdPushSub(user, id)
        // A different user's sub returns empty Optional → same ActionNotAuthorizedException
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(5L);
        dto.setLabel("sysNotes");
        dto.setValue(true);

        // mockUser does not own sub 5 — repo returns empty for this user+id combination
        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pushSubscriptionService.updatePushSub(mockUser, dto))
                .isInstanceOf(ActionNotAuthorizedException.class);
    }

    @Test
    void updatePushSub_InvalidLabel_ThrowsIllegalArgumentException() {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("badLabel");
        dto.setValue(true);

        when(pushSubRepository.findByUserAndIdPushSub(mockUser, 1L))
                .thenReturn(Optional.of(new PushSubscription()));

        assertThatThrownBy(() -> pushSubscriptionService.updatePushSub(mockUser, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── deletePushSub ────────────────────────────────────────────────────────

    @Test
    void deletePushSub_Success_ReturnsTrue() {
        when(pushSubRepository.deleteByUserAndIdPushSub(mockUser, 1L)).thenReturn(1);

        Integer result = pushSubscriptionService.deletePushSub(mockUser, 1L);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void deletePushSub_NotFound_ThrowsResourceNotFoundException() {
        when(pushSubRepository.deleteByUserAndIdPushSub(mockUser, 99L)).thenReturn(0);

        assertThatThrownBy(() -> pushSubscriptionService.deletePushSub(mockUser, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletePushSub_WrongUser_ThrowsResourceNotFoundException() {
        // Ownership enforced by repo: deleteByUserAndIdPushSub(user, id)
        // Wrong user → 0 rows deleted → ResourceNotFoundException
        when(pushSubRepository.deleteByUserAndIdPushSub(mockUser, 5L)).thenReturn(0);

        assertThatThrownBy(() -> pushSubscriptionService.deletePushSub(mockUser, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
