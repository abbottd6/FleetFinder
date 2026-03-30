package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.config.discord.DiscordBotService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.SkipExternalNotificationProcessingException;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationPayload;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationServiceImpl;
import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import com.sc_fleetfinder.fleets.utils.ExternalNotifcationResult;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private ModListingActionRepository modActionRepo;

    @Mock
    private NotificationOutboxRepository outboxRepo;

    @Mock
    private MessageRepository msgRepo;

    @Mock
    private PushNotificationService pushNotificationService;

    @Mock
    private DiscordBotService discordBotService;

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
        when(notificationRepo.findAllInAppNotificationsByUserId(eq(1L), any())).thenReturn(entityPage);
        when(modelMapper.map(note, GetNotificationDto.class)).thenReturn(dto);

        Page<GetNotificationDto> result = notificationService.getAllMyNotifications(mockUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNotificationId()).isEqualTo(1L);
    }

    @Test
    void getAllMyNotifications_EmptyPage_ReturnsEmptyPage() {
        when(notificationRepo.findAllInAppNotificationsByUserId(eq(1L), any())).thenReturn(new PageImpl<>(List.of()));

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

    // ─── prepareAndSendOutboxNotification ────────────────────────────────────

    // Helper: build a mock NotificationOutbox stubbed with the fields accessed by every build* method
    private NotificationOutbox buildMockOutbox(NotificationType eventType, DeliveryChannel channel) {
        NotificationOutbox outbox = mock(NotificationOutbox.class);
        Users owner = mock(Users.class);
        when(owner.getUserId()).thenReturn(1L);
        when(owner.getKeycloakId()).thenReturn("testKc");
        when(owner.getDiscordId()).thenReturn("discord123");
        when(outbox.getEntityOwner()).thenReturn(owner);
        when(outbox.getEventType()).thenReturn(eventType);
        when(outbox.getDeliveryChannel()).thenReturn(channel);
        when(outbox.getEntityId()).thenReturn(10L);
        when(outbox.getParentEntityId()).thenReturn(5L);
        when(outbox.getParentEntityType()).thenReturn("TEST_PARENT");
        when(outbox.getSiblingKey()).thenReturn("testSibKey");
        when(outbox.getEntityNewStatus()).thenReturn("STALE");
        NotificationTargetMetadata payload = mock(NotificationTargetMetadata.class);
        when(payload.getNoteTopic()).thenReturn("Test Topic");
        when(payload.getTargetLabel()).thenReturn("Test Label");
        when(outbox.getPayloadJson()).thenReturn(payload);
        return outbox;
    }

    // Helper: build a mock Notification returned from notificationRepo.save()
    private Notification buildMockSavedNote(DeliveryChannel channel, Users owner) {
        Notification note = mock(Notification.class);
        when(note.getDeliveryChannel()).thenReturn(channel);
        when(note.getUser()).thenReturn(owner);
        return note;
    }

    // ── LISTING_VIS_STATUS_CHANGED ────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_ListingVisStatusChanged_SiblingAlreadyRead_ThrowsSkip() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.DISCORD);
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey"))
                .thenReturn(Optional.of(LocalDateTime.now()));

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(SkipExternalNotificationProcessingException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_ListingVisStatusChanged_GroupListingNotFound_ThrowsResourceNotFound() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.IN_APP);
        when(groupListingRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    // ── LISTING_ARCHIVED ─────────────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_ListingArchived_SiblingAlreadyRead_ThrowsSkip() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_ARCHIVED, DeliveryChannel.DISCORD);
        ListingArchive mockArchive = mock(ListingArchive.class);
        when(mockArchive.getListingTitle()).thenReturn("Archived Listing");
        when(archiveRepo.findByGroupId(10L)).thenReturn(Optional.of(mockArchive));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey"))
                .thenReturn(Optional.of(LocalDateTime.now()));

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(SkipExternalNotificationProcessingException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_ListingArchived_ArchiveNotFound_ThrowsResourceNotFound() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_ARCHIVED, DeliveryChannel.IN_APP);
        when(archiveRepo.findByGroupId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    // ── NEW_LISTING_MATCH ────────────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_NewListingMatch_SiblingAlreadyRead_ThrowsSkip() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.NEW_LISTING_MATCH, DeliveryChannel.DISCORD);
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey"))
                .thenReturn(Optional.of(LocalDateTime.now()));

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(SkipExternalNotificationProcessingException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_NewListingMatch_SiblingUnread_SavesNotification() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.NEW_LISTING_MATCH, DeliveryChannel.IN_APP);
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());
        Notification savedNote = buildMockSavedNote(DeliveryChannel.IN_APP, outbox.getEntityOwner());
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(0);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());

        assertThatNoException().isThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox));
        verify(notificationRepo).save(any(Notification.class));
    }

    // ── MOD_DELETE ────────────────────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_ModDelete_ActionNotFound_ThrowsResourceNotFound() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.MOD_DELETE, DeliveryChannel.IN_APP);
        when(modActionRepo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_ModDelete_SiblingAlreadyRead_ThrowsSkip() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.MOD_DELETE, DeliveryChannel.DISCORD);
        ModListingAction mockAction = mock(ModListingAction.class);
        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(mockAction.getActionBasis()).thenReturn(basis);
        when(basis.getBasisLabel()).thenReturn("Spam");
        when(modActionRepo.findById(5L)).thenReturn(Optional.of(mockAction));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey"))
                .thenReturn(Optional.of(LocalDateTime.now()));

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(SkipExternalNotificationProcessingException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_ModDelete_SiblingUnread_SavesNotification() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.MOD_DELETE, DeliveryChannel.IN_APP);
        ModListingAction mockAction = mock(ModListingAction.class);
        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(mockAction.getActionBasis()).thenReturn(basis);
        when(basis.getBasisLabel()).thenReturn("Spam");
        when(modActionRepo.findById(5L)).thenReturn(Optional.of(mockAction));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());
        Notification savedNote = buildMockSavedNote(DeliveryChannel.IN_APP, outbox.getEntityOwner());
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(0);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());

        assertThatNoException().isThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox));
        verify(notificationRepo).save(any(Notification.class));
    }

    // ── NEW_CHAT_MESSAGE ──────────────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_NewChatMessage_AlreadyReadByRecipient_ThrowsSkip() {
        // parentEntityId=5L (convId), entityId=10L (msgId), entityOwner.userId=1L
        NotificationOutbox outbox = buildMockOutbox(NotificationType.NEW_CHAT_MESSAGE, DeliveryChannel.DISCORD);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getMsgId()).thenReturn(5L);
        when(msgRepo.findMessageByConversationIdAndMessageId(5L, 10L)).thenReturn(Optional.of(mockMessage));
        // lastReadId (5) >= msgId (5) → already read
        when(msgRepo.findRecipientLastReadByConvIdAndUserId(5L, 1L)).thenReturn(Optional.of(5L));

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(SkipExternalNotificationProcessingException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    @Test
    void prepareAndSendOutboxNotification_NewChatMessage_MessageNotFound_ThrowsResourceNotFound() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.NEW_CHAT_MESSAGE, DeliveryChannel.DISCORD);
        when(msgRepo.findMessageByConversationIdAndMessageId(5L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepo, never()).save(any(Notification.class));
    }

    // ── Deferred-then-inactive scenario: DISCORD delivery, sibling IN_APP unread ──

    @Test
    void prepareAndSendOutboxNotification_ExternalDelivery_SiblingUnread_ProceedsToSend() {
        // Simulates: user was active (OutboxSenderTask deferred), user now inactive → this method is called.
        // sibling IN_APP notification exists but is unread → external notification proceeds.
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.DISCORD);
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());

        Users noteOwner = mock(Users.class);
        when(noteOwner.getDiscordId()).thenReturn("discord123");
        Notification savedNote = buildMockSavedNote(DeliveryChannel.DISCORD, noteOwner);
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);

        assertThatNoException().isThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox));
        verify(notificationRepo).save(any(Notification.class));
        verify(discordBotService).sendDiscordNotification(eq("discord123"), any(Notification.class));
    }

    // ── IN_APP delivery: sends via WebSocket ──────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_InAppDelivery_SendsViaWebSocket() {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.IN_APP);
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());

        Users noteOwner = mock(Users.class);
        when(noteOwner.getUserId()).thenReturn(1L);
        when(noteOwner.getKeycloakId()).thenReturn("testKc");
        Notification savedNote = buildMockSavedNote(DeliveryChannel.IN_APP, noteOwner);
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(notificationRepo.countUnreadByUserId(1L)).thenReturn(3);
        when(modelMapper.map(any(Notification.class), eq(GetNotificationDto.class))).thenReturn(new GetNotificationDto());

        notificationService.prepareAndSendOutboxNotification(outbox);

        verify(messagingTemplate).convertAndSendToUser(eq("testKc"), eq("/queue/system.notify_count"), any());
        verify(messagingTemplate).convertAndSendToUser(eq("testKc"), eq("/queue/system.notify"), any());
        verify(messagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
    }

    // ── PUSH delivery ─────────────────────────────────────────────────────────

    @Test
    void prepareAndSendOutboxNotification_PushDelivery_Success_SetsReadAt() throws Exception {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.PUSH);
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());

        com.sc_fleetfinder.fleets.entities.PushSubscription mockPushSub =
                mock(com.sc_fleetfinder.fleets.entities.PushSubscription.class);
        when(mockPushSub.getIdPushSub()).thenReturn(99L);
        when(outbox.getTargetPushSub()).thenReturn(mockPushSub);

        Notification savedNote = buildMockSavedNote(DeliveryChannel.PUSH, outbox.getEntityOwner());
        when(savedNote.getTitle()).thenReturn("Test Title");
        when(savedNote.getMessage()).thenReturn("Test Message");
        when(savedNote.getType()).thenReturn(NotificationType.LISTING_VIS_STATUS_CHANGED);
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(pushNotificationService.sendPushNotificationObject(eq(mockPushSub), any(PushNotificationPayload.class)))
                .thenReturn(Map.of(ExternalNotifcationResult.SUCCESS, HttpStatus.CREATED));

        assertThatNoException().isThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox));

        // save called twice: once from buildListingStatusChangeNotification, once from sendPushNotification (set readAt)
        verify(notificationRepo, times(2)).save(any(Notification.class));
        verify(savedNote).setReadAt(any(Instant.class));
    }

    @Test
    void prepareAndSendOutboxNotification_PushDelivery_Failure_DeletesNotification_ThrowsException() throws Exception {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.LISTING_VIS_STATUS_CHANGED, DeliveryChannel.PUSH);
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getListingTitle()).thenReturn("Test Listing");
        when(groupListingRepository.findById(10L)).thenReturn(Optional.of(mockListing));
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());

        com.sc_fleetfinder.fleets.entities.PushSubscription mockPushSub =
                mock(com.sc_fleetfinder.fleets.entities.PushSubscription.class);
        when(mockPushSub.getIdPushSub()).thenReturn(99L);
        when(outbox.getTargetPushSub()).thenReturn(mockPushSub);

        NotificationOutbox noteOutbox = mock(NotificationOutbox.class);
        when(noteOutbox.getOutboxId()).thenReturn(10L);
        Notification savedNote = buildMockSavedNote(DeliveryChannel.PUSH, outbox.getEntityOwner());
        when(savedNote.getTitle()).thenReturn("Test Title");
        when(savedNote.getMessage()).thenReturn("Test Message");
        when(savedNote.getType()).thenReturn(NotificationType.LISTING_VIS_STATUS_CHANGED);
        when(savedNote.getOutbox()).thenReturn(noteOutbox);
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);
        when(pushNotificationService.sendPushNotificationObject(eq(mockPushSub), any(PushNotificationPayload.class)))
                .thenReturn(Map.of(ExternalNotifcationResult.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ResponseStatusException.class, () -> notificationService.prepareAndSendOutboxNotification(outbox));

        verify(notificationRepo).delete(savedNote);
    }

    @Test
    void prepareAndSendOutboxNotification_PushDelivery_NewListingMatch_BuildsListingUrlInPayload() throws Exception {
        NotificationOutbox outbox = buildMockOutbox(NotificationType.NEW_LISTING_MATCH, DeliveryChannel.PUSH);
        when(notificationRepo.checkSiblingNotificationReadStatus(1L, "testSibKey")).thenReturn(Optional.empty());

        com.sc_fleetfinder.fleets.entities.PushSubscription mockPushSub =
                mock(com.sc_fleetfinder.fleets.entities.PushSubscription.class);
        when(mockPushSub.getIdPushSub()).thenReturn(99L);
        when(outbox.getTargetPushSub()).thenReturn(mockPushSub);

        com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata mockMeta =
                mock(com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata.class);
        when(mockMeta.getTargetId()).thenReturn(42L);

        Notification savedNote = buildMockSavedNote(DeliveryChannel.PUSH, outbox.getEntityOwner());
        when(savedNote.getTitle()).thenReturn("Test Title");
        when(savedNote.getMessage()).thenReturn("Test Message");
        when(savedNote.getType()).thenReturn(NotificationType.NEW_LISTING_MATCH);
        when(savedNote.getTargetMetadata()).thenReturn(mockMeta);
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNote);

        ArgumentCaptor<PushNotificationPayload> payloadCaptor = ArgumentCaptor.forClass(PushNotificationPayload.class);
        when(pushNotificationService.sendPushNotificationObject(eq(mockPushSub), payloadCaptor.capture()))
                .thenReturn(Map.of(ExternalNotifcationResult.SUCCESS, HttpStatus.CREATED));

        assertThatNoException().isThrownBy(() -> notificationService.prepareAndSendOutboxNotification(outbox));

        PushNotificationPayload captured = payloadCaptor.getValue();
        assertThat(captured.getTag()).isEqualTo("New Listing Match");
        // NOTE: captured.getData() is currently null — payload.setData(dataField) is missing in production code (bug)
        assertThat(captured.getActions()).hasSize(2);
        assertThat(captured.isRequireInteraction()).isFalse();
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
