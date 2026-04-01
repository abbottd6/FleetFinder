package com.sc_fleetfinder.fleets.unit_tests.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ConversationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.UnMuteAndProvisionRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.events.NewMessageExternalNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.chat_services.ChatServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionService;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// LENIENT: ChatServiceImpl has two MessageRepository fields (msgRepo + messageRepository) —
// Mockito injects the same mock for both, causing some stubs to appear "unused" per field.
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceImplTest {

    @Mock
    private ConversationConversionService ccs;

    @Mock
    private ParticipantRepository participantRepo;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageConversionService msgConvSrv;

    @Mock
    private ConversationRepository convRepo;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChatServiceImpl chatService;

    private Users senderUser;
    private Users recipientUser;
    private Conversation conv;
    private Participant senderPart;
    private Participant recipientPart;

    @BeforeEach
    void setUp() {
        senderUser = new Users();
        senderUser.setUserId(1L);
        senderUser.setKeycloakId("sender-kc-id");
        senderUser.setUsername("senderUser");

        recipientUser = new Users();
        recipientUser.setUserId(2L);
        recipientUser.setKeycloakId("recipient-kc-id");
        recipientUser.setUsername("recipientUser");

        conv = new Conversation();
        conv.setConversationId(10L);
        conv.setTitle("Test Chat");

        senderPart = new Participant();
        senderPart.setUser(senderUser);
        senderPart.setMuting(false);
        senderPart.setArchived(false);

        recipientPart = new Participant();
        recipientPart.setUser(recipientUser);
        recipientPart.setMuting(false);
        recipientPart.setArchived(false);

        Set<Participant> participants = new HashSet<>();
        participants.add(senderPart);
        participants.add(recipientPart);
        conv.setParticipants(participants);
    }

    // ─── findMyConversations ───────────────────────────────────────────────────

    @Test
    void findMyConversations_ReturnsPageOfMappedConversationDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conversation> convPage = new PageImpl<>(List.of(conv));
        GetConversationDto expectedDto = new GetConversationDto();
        expectedDto.setConversationId(10L);

        when(participantRepo.pageConversationsByUserParticipant(senderUser, pageable)).thenReturn(convPage);
        when(ccs.convertToDto(conv, 1L)).thenReturn(expectedDto);

        Page<GetConversationDto> result = chatService.findMyConversations(senderUser, pageable);

        assertAll("findMyConversations assertions:",
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().get(0).getConversationId()).isEqualTo(10L),
                () -> verify(ccs).convertToDto(conv, 1L)
        );
    }

    // ─── findConvMessages ──────────────────────────────────────────────────────

    @Test
    void findConvMessages_WhenUserIsParticipant_ReturnsMessagePage() {
        Pageable pageable = PageRequest.of(0, 20);
        Message msg = new Message();
        Page<Message> msgPage = new PageImpl<>(List.of(msg));
        GetMessageDto msgDto = new GetMessageDto();

        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.of(senderPart));
        when(messageRepository.findMessagesByConversationId(10L, pageable)).thenReturn(msgPage);
        when(msgConvSrv.convertToDto(msg)).thenReturn(msgDto);

        Page<GetMessageDto> result = chatService.findConvMessages(senderUser, 10L, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findConvMessages_WhenUserIsNotParticipant_ThrowsRuntimeException() {
        // AccessDeniedException is thrown inside the try-catch and wrapped as RuntimeException
        Pageable pageable = PageRequest.of(0, 20);
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.findConvMessages(senderUser, 10L, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to retrieve conversation messages");
    }

    @Test
    void findConvMessages_WhenMessageRepoThrows_ThrowsRuntimeException() {
        Pageable pageable = PageRequest.of(0, 20);
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.of(senderPart));
        when(messageRepository.findMessagesByConversationId(10L, pageable)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> chatService.findConvMessages(senderUser, 10L, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to retrieve conversation messages");
    }

    // ─── findOrStartNew ────────────────────────────────────────────────────────

    @Test
    void findOrStartNew_WhenConversationExists_ReturnsDtoWithoutCreatingNew() {
        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Test Chat", 2L);

        GetConversationDto expectedDto = new GetConversationDto();
        expectedDto.setConversationId(10L);

        // Both participants returned so verifyParticipantsOrThrow passes
        when(convRepo.findByDmKey(any())).thenReturn(Optional.of(conv));
        when(participantRepo.findParticipants(eq(10L), any())).thenReturn(List.of(senderPart, recipientPart));
        when(ccs.convertToDto(any(Conversation.class), eq(1L))).thenReturn(expectedDto);

        GetConversationDto result = chatService.findOrStartNew(senderUser, dto);

        assertAll("findOrStartNew — existing conversation:",
                () -> assertThat(result.getConversationId()).isEqualTo(10L),
                () -> verify(userRepository, never()).findById(anyLong()) // no new conversation created
        );
    }

    @Test
    void findOrStartNew_WhenConversationDoesNotExist_CreatesNewConversation() {
        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Test Chat", 2L);

        GetConversationDto expectedDto = new GetConversationDto();
        when(convRepo.findByDmKey(any())).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipientUser));
        when(ccs.convertToDto(any(Conversation.class), eq(1L))).thenReturn(expectedDto);

        chatService.findOrStartNew(senderUser, dto);

        // generateNewConversation was called: convRepo.save called at least once, recipient looked up
        assertAll("findOrStartNew — new conversation created:",
                () -> verify(userRepository).findById(2L),
                () -> verify(convRepo, org.mockito.Mockito.atLeastOnce()).save(any(Conversation.class))
        );
    }

    @Test
    void findOrStartNew_WhenExistingConvAndSenderIsMuting_ThrowsConfirmationRequiredException() {
        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Test Chat", 2L);

        senderPart.setMuting(true);

        when(convRepo.findByDmKey(any())).thenReturn(Optional.of(conv));
        when(participantRepo.findParticipants(eq(10L), any())).thenReturn(List.of(senderPart, recipientPart));

        assertThatThrownBy(() -> chatService.findOrStartNew(senderUser, dto))
                .isInstanceOf(ConfirmationRequiredException.class);
    }

    @Test
    void findOrStartNew_WhenDtoTitleIsNull_ShouldNotNpe() {
        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, null, 2L); // null title triggers NPE

        when(convRepo.findByDmKey(any())).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipientUser));
        when(ccs.convertToDto(any(Conversation.class), eq(1L))).thenReturn(new GetConversationDto());

        chatService.findOrStartNew(senderUser, dto);
    }

    // ─── sendNewMessage ────────────────────────────────────────────────────────

    @Test
    void sendNewMessage_WhenConversationNotFound_ThrowsResourceNotFoundException() {
        SendMessageDto dto = buildSendDto();
        when(convRepo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.sendNewMessage(senderUser, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void sendNewMessage_WhenSenderIsMuting_ThrowsConfirmationRequiredException() {
        senderPart.setMuting(true);
        SendMessageDto dto = buildSendDto();

        when(convRepo.findById(10L)).thenReturn(Optional.of(conv));
        when(messageRepository.countMessagesByConversation_ConversationId(10L)).thenReturn(1L);

        assertThatThrownBy(() -> chatService.sendNewMessage(senderUser, dto))
                .isInstanceOf(ConfirmationRequiredException.class);
    }

    @Test
    void sendNewMessage_WhenNotFirstMessage_SavesMessagePublishesEventAndBroadcasts() {
        SendMessageDto dto = buildSendDto();
        Message savedMsg = new Message();
        savedMsg.setMsgId(50L);
        GetMessageDto msgDto = new GetMessageDto();

        when(convRepo.findById(10L)).thenReturn(Optional.of(conv));
        when(messageRepository.countMessagesByConversation_ConversationId(10L)).thenReturn(1L);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMsg);
        when(msgConvSrv.convertToDto(any(Message.class))).thenReturn(msgDto);
        when(participantRepo.userUnreadCountByUserId(2L)).thenReturn(List.of());

        GetMessageDto result = chatService.sendNewMessage(senderUser, dto);

        assertAll("sendNewMessage success assertions:",
                () -> assertThat(result).isEqualTo(msgDto),
                () -> verify(messageRepository).save(any(Message.class)),
                () -> verify(eventPublisher).publishEvent(any(NewMessageExternalNotifyEvent.class)),
                () -> verify(messagingTemplate).convertAndSendToUser(
                        eq("recipient-kc-id"), eq("/queue/chat.message"), any()),
                () -> verify(messagingTemplate).convertAndSendToUser(
                        eq("sender-kc-id"), eq("/queue/chat.message"), any()),
                () -> verify(messagingTemplate, never()).convertAndSendToUser(
                        any(), eq("/queue/chat.conversation"), any()) // not first message
        );
    }

    @Test
    void sendNewMessage_WhenFirstMessage_BroadcastsConversationToRecipient() {
        SendMessageDto dto = buildSendDto();
        Message savedMsg = new Message();
        savedMsg.setMsgId(50L);
        GetConversationDto recipientConvDto = new GetConversationDto();
        GetMessageDto msgDto = new GetMessageDto();

        when(convRepo.findById(10L)).thenReturn(Optional.of(conv));
        when(messageRepository.countMessagesByConversation_ConversationId(10L)).thenReturn(0L); // first message
        when(messageRepository.save(any(Message.class))).thenReturn(savedMsg);
        when(msgConvSrv.convertToDto(any(Message.class))).thenReturn(msgDto);
        when(participantRepo.userUnreadCountByUserId(2L)).thenReturn(List.of());
        when(ccs.convertToDto(any(Conversation.class), eq(2L))).thenReturn(recipientConvDto);

        chatService.sendNewMessage(senderUser, dto);

        verify(messagingTemplate).convertAndSendToUser(
                eq("recipient-kc-id"), eq("/queue/chat.conversation"), eq(recipientConvDto));
    }

    @Test
    void sendNewMessage_WithValidReplyToId_FindsAndAttachesOriginalMessage() {
        SendMessageDto dto = buildSendDto();
        dto.setRepliedToMsgId(30L);

        Message replyTarget = new Message();
        replyTarget.setMsgId(30L);
        Message savedMsg = new Message();
        GetMessageDto msgDto = new GetMessageDto();

        when(convRepo.findById(10L)).thenReturn(Optional.of(conv));
        when(messageRepository.countMessagesByConversation_ConversationId(10L)).thenReturn(1L);
        when(messageRepository.findMessageByConversationIdAndMessageId(10L, 30L)).thenReturn(Optional.of(replyTarget));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMsg);
        when(msgConvSrv.convertToDto(any(Message.class))).thenReturn(msgDto);
        when(participantRepo.userUnreadCountByUserId(2L)).thenReturn(List.of());

        chatService.sendNewMessage(senderUser, dto);

        verify(messageRepository).findMessageByConversationIdAndMessageId(10L, 30L);
    }

    @Test
    void sendNewMessage_WithInvalidReplyToId_ThrowsResourceNotFoundException() {
        SendMessageDto dto = buildSendDto();
        dto.setRepliedToMsgId(999L);

        when(convRepo.findById(10L)).thenReturn(Optional.of(conv));
        when(messageRepository.countMessagesByConversation_ConversationId(10L)).thenReturn(1L);
        when(messageRepository.findMessageByConversationIdAndMessageId(10L, 999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.sendNewMessage(senderUser, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── archiveConv ───────────────────────────────────────────────────────────

    @Test
    void archiveConv_WhenParticipantFound_SetsArchivedTrue() {
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.of(senderPart));

        chatService.archiveConv(senderUser, 10L);

        assertAll("archiveConv success:",
                () -> assertThat(senderPart.isArchived()).isTrue(),
                () -> verify(participantRepo).save(senderPart)
        );
    }

    @Test
    void archiveConv_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.archiveConv(senderUser, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── muteConv ──────────────────────────────────────────────────────────────

    @Test
    void muteConv_WhenParticipantFound_SetsMutingTrue() {
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.of(senderPart));

        chatService.muteConv(senderUser, 10L);

        assertAll("muteConv success:",
                () -> assertThat(senderPart.isMuting()).isTrue(),
                () -> verify(participantRepo).save(senderPart)
        );
    }

    @Test
    void muteConv_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.muteConv(senderUser, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── unMuteConversation ────────────────────────────────────────────────────

    @Test
    void unMuteConversation_WhenParticipantFound_ClearsMutingAndReturnsProvDto() {
        senderPart.setMuting(true);

        UnMuteAndProvisionRequestDto dto = new UnMuteAndProvisionRequestDto();
        dto.setConversationId(10L);
        dto.setConvType(ConversationType.DIRECT);
        dto.setTitle("Test Chat");
        dto.setRecipientId(2L);

        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.of(senderPart));

        FindOrStartNewConversationDto result = chatService.unMuteConversation(senderUser, dto);

        assertAll("unMuteConversation success:",
                () -> assertThat(senderPart.isMuting()).isFalse(),
                () -> verify(participantRepo).save(senderPart),
                () -> assertThat(result.getRecipientId()).isEqualTo(2L),
                () -> assertThat(result.getConvType()).isEqualTo(ConversationType.DIRECT)
        );
    }

    @Test
    void unMuteConversation_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        UnMuteAndProvisionRequestDto dto = new UnMuteAndProvisionRequestDto();
        dto.setConversationId(10L);
        dto.setRecipientId(2L);

        when(participantRepo.findByConversationAndUser(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.unMuteConversation(senderUser, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getUserUnreadCounts ───────────────────────────────────────────────────

    @Test
    void getUserUnreadCounts_WithUnreadMessages_ReturnsDtoWithCorrectTotal() {
        List<ConvUnreadMap> unreadData = List.of(
                new ConvUnreadMap(5L, 3L),
                new ConvUnreadMap(6L, 2L)
        );
        when(participantRepo.userUnreadCountByUserId(1L)).thenReturn(unreadData);

        UserUnreadResponseDto result = chatService.getUserUnreadCounts(1L);

        assertAll("getUserUnreadCounts with unread messages:",
                () -> assertThat(result.totalUnread()).isEqualTo(5L),
                () -> assertThat(result.unreadByConv()).hasSize(2)
        );
    }

    @Test
    void getUserUnreadCounts_WithZeroUnreadCounts_ExcludesZeroCountsFromResult() {
        List<ConvUnreadMap> unreadData = List.of(
                new ConvUnreadMap(5L, 0L), // zero — should be filtered out
                new ConvUnreadMap(6L, 2L)
        );
        when(participantRepo.userUnreadCountByUserId(1L)).thenReturn(unreadData);

        UserUnreadResponseDto result = chatService.getUserUnreadCounts(1L);

        assertAll("getUserUnreadCounts filtering zero counts:",
                () -> assertThat(result.totalUnread()).isEqualTo(2L),
                () -> assertThat(result.unreadByConv()).hasSize(1)
        );
    }

    @Test
    void getUserUnreadCounts_WithNoUnread_ReturnsZeroTotal() {
        when(participantRepo.userUnreadCountByUserId(1L)).thenReturn(List.of());

        UserUnreadResponseDto result = chatService.getUserUnreadCounts(1L);

        assertAll("getUserUnreadCounts empty result:",
                () -> assertThat(result.totalUnread()).isEqualTo(0L),
                () -> assertThat(result.unreadByConv()).isEmpty()
        );
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private SendMessageDto buildSendDto() {
        SendMessageDto dto = new SendMessageDto();
        dto.setConversationId(10L);
        dto.setMsgBody("Hello!");
        dto.setMessageType("TEXT");
        return dto;
    }
}
