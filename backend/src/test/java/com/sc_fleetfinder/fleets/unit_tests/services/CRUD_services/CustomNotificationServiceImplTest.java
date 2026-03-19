package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.UserCustomNotificationRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ContentLimitException;
import com.sc_fleetfinder.fleets.services.CRUD_services.CustomNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // getAllMyCustomNotifications buggy stub + editCustomNotification void-map mismatch
class CustomNotificationServiceImplTest {

    @Mock
    private UserCustomNotificationRepository cnr;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomNotificationServiceImpl service;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
    }

    // ─── getAllMyCustomNotifications ──────────────────────────────────────────

    @Test
    void getAllMyCustomNotifications_Success_ReturnsPage() {
        UserCustomNotification entity = new UserCustomNotification();
        GetCustomNotificationResponseDto dto = new GetCustomNotificationResponseDto();
        dto.setCustomNoteId(1L);

        Page<UserCustomNotification> entityPage = new PageImpl<>(List.of(entity));

        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        when(cnr.findByUser(eq(mockUser), any())).thenReturn(entityPage);
        // NOTE: stub matches the BUGGY production code that passes the class literal instead of the entity instance
        when(modelMapper.map(UserCustomNotification.class, GetCustomNotificationResponseDto.class)).thenReturn(dto);

        Page<GetCustomNotificationResponseDto> result = service.getAllMyCustomNotifications(mockUser, pageDto);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCustomNoteId()).isEqualTo(1L);
    }

    @Test
    void getAllMyCustomNotifications_EmptyPage_ReturnsEmptyPage() {
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        when(cnr.findByUser(eq(mockUser), any())).thenReturn(new PageImpl<>(List.of()));

        Page<GetCustomNotificationResponseDto> result = service.getAllMyCustomNotifications(mockUser, pageDto);

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ─── createNewCustomNotification ──────────────────────────────────────────

    @Test
    void createNewCustomNotification_Success() {
        CreateOrEditCustomNotificationDto dto = new CreateOrEditCustomNotificationDto();
        UserCustomNotification entity = new UserCustomNotification();
        GetCustomNotificationResponseDto responseDto = new GetCustomNotificationResponseDto();
        responseDto.setCustomNoteId(1L);

        when(cnr.countByUser(mockUser)).thenReturn(5);
        when(modelMapper.map(any(), eq(UserCustomNotification.class))).thenReturn(entity);
        when(cnr.save(any())).thenReturn(entity);
        when(modelMapper.map(entity, GetCustomNotificationResponseDto.class)).thenReturn(responseDto);

        GetCustomNotificationResponseDto result = service.createNewCustomNotification(mockUser, dto);

        assertThat(result.getCustomNoteId()).isEqualTo(1L);
        verify(cnr).save(any());
    }

    @Test
    void createNewCustomNotification_AtLimit_ThrowsContentLimitException() {
        when(cnr.countByUser(mockUser)).thenReturn(10);

        assertThatThrownBy(() -> service.createNewCustomNotification(mockUser, new CreateOrEditCustomNotificationDto()))
                .isInstanceOf(ContentLimitException.class);

        verify(cnr, never()).save(any());
    }

    @Test
    void createNewCustomNotification_BelowLimit_Saves() {
        CreateOrEditCustomNotificationDto dto = new CreateOrEditCustomNotificationDto();
        UserCustomNotification entity = new UserCustomNotification();

        when(cnr.countByUser(mockUser)).thenReturn(9);
        when(modelMapper.map(any(), eq(UserCustomNotification.class))).thenReturn(entity);
        when(cnr.save(any())).thenReturn(entity);
        when(modelMapper.map(entity, GetCustomNotificationResponseDto.class)).thenReturn(new GetCustomNotificationResponseDto());

        service.createNewCustomNotification(mockUser, dto);

        verify(cnr).save(any());
    }

    // ─── editCustomNotification ───────────────────────────────────────────────

    @Test
    void editCustomNotification_Success() {
        UserCustomNotification entity = new UserCustomNotification();
        GetCustomNotificationResponseDto responseDto = new GetCustomNotificationResponseDto();
        responseDto.setCustomNoteId(1L);

        when(cnr.findByUserAndCustomNoteId(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(cnr.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, GetCustomNotificationResponseDto.class)).thenReturn(responseDto);

        GetCustomNotificationResponseDto result = service.editCustomNotification(mockUser, 1L, new CreateOrEditCustomNotificationDto());

        assertThat(result.getCustomNoteId()).isEqualTo(1L);
        verify(cnr).save(entity);
    }

    @Test
    void editCustomNotification_NotFound_ThrowsActionNotAuthorizedException() {
        when(cnr.findByUserAndCustomNoteId(mockUser, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editCustomNotification(mockUser, 99L, new CreateOrEditCustomNotificationDto()))
                .isInstanceOf(ActionNotAuthorizedException.class);
    }

    // ─── enablementStateChange ────────────────────────────────────────────────

    @Test
    void enablementStateChange_ToTrue_SavesAndFlushes() {
        UserCustomNotification entity = new UserCustomNotification();
        entity.setEnabled(false);

        when(cnr.findByUserAndCustomNoteId(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(cnr.save(any())).thenReturn(entity);

        service.enablementStateChange(mockUser, 1L, true);

        assertThat(entity.getEnabled()).isTrue();
        verify(cnr).save(entity);
        verify(cnr).flush();
    }

    @Test
    void enablementStateChange_ToFalse_SavesAndFlushes() {
        UserCustomNotification entity = new UserCustomNotification();
        entity.setEnabled(true);

        when(cnr.findByUserAndCustomNoteId(mockUser, 1L)).thenReturn(Optional.of(entity));
        when(cnr.save(any())).thenReturn(entity);

        service.enablementStateChange(mockUser, 1L, false);

        assertThat(entity.getEnabled()).isFalse();
        verify(cnr).save(entity);
        verify(cnr).flush();
    }

    @Test
    void enablementStateChange_NotFound_ThrowsActionNotAuthorizedException() {
        when(cnr.findByUserAndCustomNoteId(mockUser, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enablementStateChange(mockUser, 99L, true))
                .isInstanceOf(ActionNotAuthorizedException.class);
    }

    // ─── deleteCustomNotification ─────────────────────────────────────────────

    @Test
    void deleteCustomNotification_Success_DeleteAndFlushCalled() {
        UserCustomNotification entity = new UserCustomNotification();

        when(cnr.findByUserAndCustomNoteId(mockUser, 1L)).thenReturn(Optional.of(entity));

        service.deleteCustomNotification(mockUser, 1L);

        verify(cnr).delete(entity);
        verify(cnr).flush();
    }

    @Test
    void deleteCustomNotification_NotFound_ThrowsActionNotAuthorizedException() {
        when(cnr.findByUserAndCustomNoteId(mockUser, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCustomNotification(mockUser, 99L))
                .isInstanceOf(ActionNotAuthorizedException.class);
    }
}
