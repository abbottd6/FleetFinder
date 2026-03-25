package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationServiceImpl;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // SimpMessagingTemplate stubs unused in non-WS paths
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GroupListingRepository groupListingRepository;

    @Mock
    private ListingArchiveRepository archiveRepo;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setKeycloakId("mockKcId");
    }

    // ─── getMyNotifications ───────────────────────────────────────────────────

    @Test
    void getMyDropdownNotifications_Success_ReturnsMappedPage() {
        Notification note = new Notification();
        GetNotificationDto dto = new GetNotificationDto();
        dto.setNotificationId(1L);

        Page<Notification> entityPage = new PageImpl<>(List.of(note));
        when(notificationRepo.findAllDropdownNotificationsByUserId(eq(1L), any())).thenReturn(entityPage);
        when(modelMapper.map(note, GetNotificationDto.class)).thenReturn(dto);

        Page<GetNotificationDto> result = notificationService.getMyDropdownNotifications(mockUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNotificationId()).isEqualTo(1L);
    }

    @Test
    void getMyDropdownNotifications_EmptyPage_ReturnsEmptyPage() {
        when(notificationRepo.findAllDropdownNotificationsByUserId(eq(1L), any())).thenReturn(new PageImpl<>(List.of()));

        Page<GetNotificationDto> result = notificationService.getMyDropdownNotifications(mockUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ─── getAllMyNotifications ────────────────────────────────────────────────

    @Test
    void getAllMyNotifications_Success_ReturnsMappedPage() {
        Notification note = new Notification();
        GetNotificationDto dto = new GetNotificationDto();
        dto.setNotificationId(1L);

        Page<Notification> entityPage = new PageImpl<>(List.of(note));
        when(notificationRepo.findAllNotificationsByUserId(eq(1L), any())).thenReturn(entityPage);
        when(modelMapper.map(note, GetNotificationDto.class)).thenReturn(dto);

        Page<GetNotificationDto> result = notificationService.getAllMyNotifications(mockUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNotificationId()).isEqualTo(1L);
    }

    @Test
    void getAllMyNotifications_EmptyPage_ReturnsEmptyPage() {
        when(notificationRepo.findAllNotificationsByUserId(eq(1L), any())).thenReturn(new PageImpl<>(List.of()));

        Page<GetNotificationDto> result = notificationService.getAllMyNotifications(mockUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ─── removeDropdownPriority ───────────────────────────────────────────────

    @Test
    void removeDropdownPriority_Success_SetsFlagToFalseAndSaves() {
        Users owner = new Users();
        owner.setUserId(1L);

        Notification note = new Notification();
        note.setUser(owner);
        note.setDropdownPriority(true);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(note));
        when(notificationRepo.save(any())).thenReturn(note);

        notificationService.removeDropdownPriority(mockUser, 1L);

        assertThat(note.getDropdownPriority()).isFalse();
        verify(notificationRepo).save(note);
    }

    @Test
    void removeDropdownPriority_NotFound_ThrowsResourceNotFoundException() {
        when(notificationRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.removeDropdownPriority(mockUser, 99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(notificationRepo, never()).save(any());
    }

    @Test
    void removeDropdownPriority_WrongUser_ThrowsActionNotAuthorizedException() {
        Users otherUser = new Users();
        otherUser.setUserId(2L);

        Notification note = new Notification();
        note.setUser(otherUser);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(note));

        assertThatThrownBy(() -> notificationService.removeDropdownPriority(mockUser, 1L))
                .isInstanceOf(ActionNotAuthorizedException.class);

        verify(notificationRepo, never()).save(any());
    }

    // ─── deleteNotification ───────────────────────────────────────────────────

    @Test
    void deleteNotification_Success_DeletesById() {
        Users owner = new Users();
        owner.setUserId(1L);

        Notification note = new Notification();
        note.setUser(owner);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(note));

        notificationService.deleteNotification(mockUser, 1L);

        verify(notificationRepo).deleteById(1L);
    }

    @Test
    void deleteNotification_NotFound_ThrowsResourceNotFoundException() {
        when(notificationRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.deleteNotification(mockUser, 99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(notificationRepo, never()).deleteById(any());
    }

    @Test
    void deleteNotification_WrongUser_ThrowsActionNotAuthorizedException() {
        Users otherUser = new Users();
        otherUser.setUserId(2L);

        Notification note = new Notification();
        note.setUser(otherUser);

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(note));

        assertThatThrownBy(() -> notificationService.deleteNotification(mockUser, 1L))
                .isInstanceOf(ActionNotAuthorizedException.class);

        verify(notificationRepo, never()).deleteById(any());
    }

    // ─── deleteAllNotifications ───────────────────────────────────────────────

    @Test
    void deleteAllNotifications_ReturnsCountFromRepo() {
        when(notificationRepo.deleteAllByUser_userId(1L)).thenReturn(5);

        Integer result = notificationService.deleteAllNotifications(mockUser);

        assertThat(result).isEqualTo(5);
    }

    // ─── updateRead ───────────────────────────────────────────────────────────

    @Test
    void updateRead_ReturnsClearedCount() {
        List<Long> readIds = List.of(1L, 2L, 3L);
        ReceiveReadNotesDto readDto = new ReceiveReadNotesDto(readIds);

        when(notificationRepo.markAsRead(1L, readIds)).thenReturn(3);

        Integer result = notificationService.updateRead(mockUser, readDto);

        assertThat(result).isEqualTo(3);
    }

    // ─── countUnread ──────────────────────────────────────────────────────────

    @Test
    void countUnread_ReturnsCountFromRepo() {
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(7);

        Integer result = notificationService.countUnread(1L);

        assertThat(result).isEqualTo(7);
    }

    // ─── sendOutboxNotification ───────────────────────────────────────────────

    @Test
    @Disabled("sendOutboxNotification is being reworked")
    void prepareAndSendOutboxNotification_VisStatusChanged_LooksUpListingAndSendsWsMessages() {
        Users owner = new Users();
        owner.setUserId(1L);
        owner.setKeycloakId("ownerKcId");

        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setEventType(NotificationType.LISTING_VIS_STATUS_CHANGED);
        outbox.setEntityId(10L);
        outbox.setEntityOwner(owner);
        outbox.setEntityNewStatus("STALE");

        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));

        Notification savedNote = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(3);

        notificationService.prepareAndSendOutboxNotification(outbox);

        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify_count"), any());
        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify"), any());
        verify(messagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    @Disabled("sendOutboxNotification is being reworked")
    void prepareAndSendOutboxNotification_ListingArchived_LooksUpArchiveAndSendsWsMessages() {
        Users owner = new Users();
        owner.setUserId(1L);
        owner.setKeycloakId("ownerKcId");

        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setEventType(NotificationType.LISTING_ARCHIVED);
        outbox.setEntityId(10L);
        outbox.setEntityOwner(owner);
        outbox.setEntityNewStatus("ARCHIVED");

        ListingArchive mockArchive = mock(ListingArchive.class);
        when(mockArchive.getListingTitle()).thenReturn("Archived Listing");
        when(archiveRepo.findByGroupId(10L)).thenReturn(Optional.of(mockArchive));

        Notification savedNote = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(2);

        notificationService.prepareAndSendOutboxNotification(outbox);

        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify_count"), any());
        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify"), any());
    }

    @Test
    @Disabled("sendOutboxNotification is being reworked")
    void prepareAndSendOutboxNotification_ModDelete_SkipsLookupAndSendsWsMessages() {
        Users owner = new Users();
        owner.setUserId(1L);
        owner.setKeycloakId("ownerKcId");

        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setEventType(NotificationType.MOD_DELETE);
        outbox.setEntityId(10L);
        outbox.setEntityOwner(owner);
        outbox.setEntityNewStatus("DELETED");

        Notification savedNote = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(1);

        notificationService.prepareAndSendOutboxNotification(outbox);

        verify(groupListingRepository, never()).findById(any());
        verify(archiveRepo, never()).findByGroupId(any());
        verify(notificationRepo).save(any(Notification.class));
        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify_count"), any());
        verify(messagingTemplate).convertAndSendToUser(eq("ownerKcId"), eq("/queue/system.notify"), any());
    }

    @Test
    @Disabled("sendOutboxNotification is being reworked")
    void prepareAndSendOutboxNotification_ListingArchived_ArchiveNotFound_ThrowsResourceNotFoundException() {
        Users owner = new Users();
        owner.setUserId(1L);
        owner.setKeycloakId("ownerKcId");

        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setEventType(NotificationType.LISTING_ARCHIVED);
        outbox.setEntityId(999L);
        outbox.setEntityOwner(owner);

        when(archiveRepo.findByGroupId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(notificationRepo, never()).save(any());
    }

    // ─── generateOutboxNotificationForModAction ───────────────────────────────

    @Test
    void generateOutboxNotificationForModAction_DelegatesToRepoWithActionId() {
        ModListingAction action = mock(ModListingAction.class);
        when(action.getActionId()).thenReturn(42L);

        notificationService.generateOutboxNotificationForModAction(action);

        verify(notificationRepo).generateOutboxNotificationsOnModListingDelete(42L);
        verify(notificationRepo, never()).save(any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    // ─── onUserAccountDeleted ─────────────────────────────────────────────────

    @Test
    void onUserAccountDeleted_CallsDeleteAllByUserId() {
        Users deletedUser = new Users();
        deletedUser.setUserId(5L);
        UserAccountDeleteEvent event = new UserAccountDeleteEvent(deletedUser);

        notificationService.onUserAccountDeleted(event);

        verify(notificationRepo).deleteAllByUser_userId(5L);
    }

    @Test
    void onUserAccountDeleted_RepoThrows_ExceptionSwallowed() {
        Users deletedUser = new Users();
        deletedUser.setUserId(5L);
        UserAccountDeleteEvent event = new UserAccountDeleteEvent(deletedUser);

        when(notificationRepo.deleteAllByUser_userId(5L)).thenThrow(new RuntimeException("DB error"));

        // Exception is caught and logged — must not propagate
        assertThatNoException().isThrownBy(() -> notificationService.onUserAccountDeleted(event));
    }
}
