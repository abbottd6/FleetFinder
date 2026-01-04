package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ConversationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadPerConvDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadTotalDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionService;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantRole;
import com.sc_fleetfinder.fleets.utils.MessageUserRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.utils.DmKeyUtil.sha256DmKey;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ConversationConversionService ccs;
    private final ParticipantRepository participantRepo;
    private final MessageRepository msgRepo;
    private final MessageConversionService msgConvSrv;
    private final ConversationRepository convRepo;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    ChatServiceImpl(ConversationConversionService ccs,
                    ParticipantRepository participantRepo,
                    MessageRepository msgRepo,
                    MessageConversionService msgConvSrv,
                    ConversationRepository convRepo,
                    MessageRepository messageRepository,
                    UserRepository userRepository,
                    SimpMessagingTemplate messagingTemplate) {
        this.ccs = ccs;
        this.msgRepo = msgRepo;
        this.msgConvSrv = msgConvSrv;
        this.participantRepo = participantRepo;
        this.convRepo = convRepo;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetConversationDto> findMyConversations(Users user, Pageable pageable) {
        return participantRepo.pageConversationsByUserParticipant(user, pageable)
                .map(conv -> ccs.convertToDto(conv, user.getUserId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetMessageDto> findConvMessages(Users user, Long convId, Pageable pageable) {
        try {
            if(participantRepo.findByConversationAndUser(convId, user.getUserId()).isPresent()) {
                return msgRepo.findMessagesByConversationId(convId, pageable).map(msgConvSrv::convertToDto);
            } else {
                throw (new AccessDeniedException("You are not a participant of this conversation."));
            }
        }
        catch (Exception e) {
            log.error("Unable to retrieve messages for Conversation with id {}: {}", convId, e.getMessage());
            throw new RuntimeException("Unable to retrieve conversation messages.");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GetConversationDto findOrStartNew(Users user, FindOrStartNewConversationDto dto) {
        String dmKey = sha256DmKey(user.getUserId(), dto.getRecipientId());

        Conversation conv = convRepo.findByDmKey(dmKey)
                .map(existing -> {
                    verifyParticipantsOrThrow(user.getUserId(), dto.getRecipientId(), existing);
                    if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
                    return existing;
                })
                .orElseGet(() -> generateNewConversation(user, dto, dmKey));

        convRepo.save(conv);

        return ccs.convertToDto(conv, user.getUserId());
    }

    @Override
    @Transactional
    public GetMessageDto sendNewMessage(Users user, SendMessageDto dto) {
        Conversation currentConv = convRepo.findById(dto.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        boolean isFirstMsg = (messageRepository.countMessagesByConversation_ConversationId(
                currentConv.getConversationId()) == 0);

        Map<MessageUserRoles, Participant> inferredParts = this.defineSenderAndRecipient(currentConv, user.getUserId());

        Participant senderPart = inferredParts.get(MessageUserRoles.SENDER);
        Participant recipientPart = inferredParts.get(MessageUserRoles.RECIPIENT);

        if(senderPart.isArchived() || senderPart.isMuting()) {
            throw new ConfirmationRequiredException(
                    "You muted or archived this conversation. Undo to resume?");
        }

        for( Participant part : currentConv.getParticipants()) {
            part.setArchived(false);
        }

        Message repliedToMessage = null;
        Long repliedToId = dto.getRepliedToMsgId();

        if(repliedToId != null) {
            repliedToMessage = messageRepository.findMessageByConversationIdAndMessageId(
                    currentConv.getConversationId(), repliedToId).orElseThrow(() -> new ResourceNotFoundException(
                            "repliedToMessage not found in this conversation."));
        }

        Message newMsg = new Message(currentConv, senderPart.getUser(), repliedToMessage, dto);

        messageRepository.save(newMsg);

        currentConv.setLastMsg(newMsg);
        convRepo.save(currentConv);

        GetMessageDto msgDto = msgConvSrv.convertToDto(newMsg);

        UserUnreadPerConvDto perConvDto = this.getUserUnreadCountPerConversation(recipientPart.getUser().getUserId());
        UserUnreadTotalDto recipientUnreadDto = this.updateUserUnreadTotal(perConvDto);

        if(isFirstMsg) {
            GetConversationDto recipientConvDto = this.ccs.convertToDto(currentConv, recipientPart.getUser().getUserId());

            messagingTemplate.convertAndSendToUser(
                    recipientPart.getUser().getKeycloakId(),
                    "/queue/chat.conversation",
                    recipientConvDto
            );
        }

        messagingTemplate.convertAndSendToUser(
                recipientPart.getUser().getKeycloakId(),
                "/queue/chat.unread",
                recipientUnreadDto
        );

        messagingTemplate.convertAndSendToUser(
                recipientPart.getUser().getKeycloakId(),
                "/queue/chat.message",
                msgDto
        );

        messagingTemplate.convertAndSendToUser(
                senderPart.getUser().getKeycloakId(),
                "/queue/chat.message",
                msgDto
        );

        return msgDto;
    }

    @Override
    @Transactional
    public Conversation generateNewConversation(Users user,
                                                FindOrStartNewConversationDto dto,
                                                String dmKey) {
        Conversation newConv = new Conversation(user, dto, dmKey);

        convRepo.save(newConv);

        Users recipientUser = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Participant sender = new Participant(
                user,
                newConv,
                ConversationParticipantRole.MEMBER
        );

        // recipient set to archived, so it does not show up until initiator
        // sends a message
        Participant recipient = new Participant(
                recipientUser,
                newConv,
                ConversationParticipantRole.MEMBER,
                true);

        participantRepo.save(sender);
        participantRepo.save(recipient);

        newConv.getParticipants().add(sender);
        newConv.getParticipants().add(recipient);
        convRepo.save(newConv);

        convRepo.flush();
        participantRepo.flush();

        return newConv;
    }

    @Override
    @Transactional(readOnly = true)
    public UserUnreadPerConvDto getUserUnreadCountPerConversation(Long userId) {

        Set<ConvUnreadMap> perConvUnread =
                new HashSet<>(participantRepo.userUnreadCountByUserId(userId));

        return new UserUnreadPerConvDto(userId, perConvUnread);
    }

    private UserUnreadTotalDto updateUserUnreadTotal(UserUnreadPerConvDto dto) {
        long totalUnread = 0L;

        for(ConvUnreadMap conv : dto.convIdAndUnread()) {
            totalUnread += conv.unreadCount();
        }

        return new UserUnreadTotalDto(dto.userId(), totalUnread);
    }

    private void verifyParticipantsOrThrow(Long senderId, Long recipientId, Conversation conv) {
        List<Long> requestedIds = List.of(senderId, recipientId);

        List<Participant> participants = participantRepo.findParticipants(
                conv.getConversationId(), requestedIds
        );

        Set<Long> existingUserParticipantIds = participants.stream()
                .map(part -> part.getUser().getUserId())
                .collect(Collectors.toSet());

        List<Long> missing = requestedIds.stream()
                .filter(id -> !existingUserParticipantIds.contains(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new ConversationIntegrityException(
                    "Conversation with ID: " + conv.getConversationId() +
                            "is missing the following purported participants: " + missing);
        }

        Participant sender = participants.stream()
                .filter(part -> Objects.equals(part.getUser().getUserId(), senderId))
                .findFirst()
                .orElseThrow(() -> new ConversationIntegrityException(
                        "Conversation " + conv.getConversationId() +
                                " is missing the sender participant " + senderId));

        if (sender.isMuting()) {
            throw new ConfirmationRequiredException(
                    "You muted this conversation. Undo to resume?");
        }

        sender.setLastActiveAt(Instant.now());
        participantRepo.save(sender);
    }

    private Map<MessageUserRoles, Participant> defineSenderAndRecipient(Conversation conv, Long currentUserId) {
        Participant recipient = conv.getParticipants().stream()
                .filter(participant -> !Objects.equals(
                        participant.getUser().getUserId(), currentUserId))
                .findFirst()
                .orElseThrow(() -> new ConversationIntegrityException("Conversation with ID: " +
                        conv.getConversationId() + " is missing the other participant."));

        Participant sender = conv.getParticipants().stream()
                .filter(participant -> Objects.equals(
                        participant.getUser().getUserId(), currentUserId))
                .findFirst()
                .orElseThrow(() -> new ConversationIntegrityException("Conversation with ID: " +
                        conv.getConversationId() + " is missing the current participant."));

        Map<MessageUserRoles, Participant> inferredParts = new HashMap<>();

        inferredParts.put(MessageUserRoles.SENDER, sender);
        inferredParts.put(MessageUserRoles.RECIPIENT, recipient);

        return inferredParts;
    }
}
