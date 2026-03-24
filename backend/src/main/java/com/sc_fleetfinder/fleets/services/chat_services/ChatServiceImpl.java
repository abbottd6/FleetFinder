package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ConversationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.UnMuteAndProvisionRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.ConfirmUnmuteConvDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.events.NewMessageExternalNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionService;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantRole;
import com.sc_fleetfinder.fleets.utils.MessageUserRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.utils.DmKeyUtil.sha256DmKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {



    private final ConversationConversionService ccs;
    private final ParticipantRepository participantRepo;
    private final MessageRepository msgRepo;
    private final MessageConversionService msgConvSrv;
    private final ConversationRepository convRepo;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
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

        if(senderPart.isMuting()) {
            ConfirmUnmuteConvDto unmuteDto = buildUnmuteResponse(
                    inferredParts.values().stream().toList(),
                    recipientPart.getUser().getUserId(),
                    currentConv
            );

            throw new ConfirmationRequiredException("Muting", unmuteDto);
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

        Message saved = messageRepository.save(newMsg);

        currentConv.setLastMsg(newMsg);
        convRepo.save(currentConv);

        eventPublisher.publishEvent(new NewMessageExternalNotifyEvent(recipientPart.getUser(), saved));

        GetMessageDto msgDto = msgConvSrv.convertToDto(newMsg);

        UserUnreadResponseDto recipientUnreadDto = this.getUserUnreadCounts(recipientPart.getUser().getUserId());

        for(ConvUnreadMap conv : recipientUnreadDto.unreadByConv()){
            log.debug("ConvId: {}, Unread: {}", conv.conversationId(), conv.unreadCount());
        }

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
    public UserUnreadResponseDto getUserUnreadCounts(Long userId) {

        Set<ConvUnreadMap> perConvUnread =
                new HashSet<>(participantRepo.userUnreadCountByUserId(userId));

        return updateUserUnreadTotal(perConvUnread);
    }

    @Override
    @Transactional
    public void archiveConv(Users user, Long convId) {
        participantRepo.findByConversationAndUser(convId, user.getUserId())
                .ifPresentOrElse(convPart -> {
                    convPart.setArchived(true);
                    participantRepo.save(convPart);
                }, () -> {
                    throw new ResourceNotFoundException(
                            "Conversation Record for user: " + user.getUserId() +
                            " and conversation: " + convId + " could not be found."
                    );
                });
    }

    @Override
    public void muteConv(Users user, Long convId) {
        participantRepo.findByConversationAndUser(convId, user.getUserId())
                .ifPresentOrElse(convPart -> {
                    convPart.setMuting(true);
                    participantRepo.save(convPart);
                }, () -> {
                    throw new ResourceNotFoundException(
                            "Conversation Record for user: " + user.getUserId() +
                            " and conversation: " + convId + " could not be found."
                    );
                });
    }

    private UserUnreadResponseDto updateUserUnreadTotal(Set<ConvUnreadMap> perConvUnread) {
        long totalUnread = 0L;
        Set<ConvUnreadMap> simplifiedPerConvUnread = new HashSet<>();

        for(ConvUnreadMap conv : perConvUnread) {
            if(conv.unreadCount() > 0) {
                simplifiedPerConvUnread.add(conv);
                totalUnread += conv.unreadCount();
            }
        }

        return new UserUnreadResponseDto(totalUnread, simplifiedPerConvUnread);
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
            ConfirmUnmuteConvDto unmuteDto = buildUnmuteResponse(participants, recipientId, conv);

            throw new ConfirmationRequiredException("Muting", unmuteDto);
        }

        sender.setArchived(false);
        sender.setLastActiveAt(Instant.now());
        participantRepo.save(sender);
    }

    private ConfirmUnmuteConvDto buildUnmuteResponse(List<Participant> parts,
                                                     Long recipientId,
                                                     Conversation conv) {
        Users mutedParticipant = parts.stream()
                .filter(part -> Objects.equals(recipientId, part.getUser().getUserId()))
                .findFirst()
                .map(Participant::getUser)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", recipientId));

        return new ConfirmUnmuteConvDto(mutedParticipant, conv);
    }

    @Override
    public FindOrStartNewConversationDto unMuteConversation(Users user, UnMuteAndProvisionRequestDto dto) {
        participantRepo.findByConversationAndUser(dto.getConversationId(), user.getUserId())
                .ifPresentOrElse(sender -> {
                    sender.setMuting(false);
                    participantRepo.save(sender);
                }, () -> {
                    throw new ResourceNotFoundException(
                            "Conversation Record for user: " + user.getUserId() +
                                    " and conversation: " + dto.getConversationId() + " could not be found."
                    );
                });

        return new FindOrStartNewConversationDto(dto.getConvType(), dto.getTitle(), dto.getRecipientId());
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
