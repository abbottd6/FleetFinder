package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.chat.ConversationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionService;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    ChatServiceImpl(ConversationConversionService ccs,
                    ParticipantRepository participantRepo,
                    MessageRepository msgRepo,
                    MessageConversionService msgConvSrv,
                    ConversationRepository convRepo, MessageRepository messageRepository) {
        this.ccs = ccs;
        this.msgRepo = msgRepo;
        this.msgConvSrv = msgConvSrv;
        this.participantRepo = participantRepo;
        this.convRepo = convRepo;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetConversationDto> findMyConversations(Users user, Pageable pageable) {
        return participantRepo.findConversationsByUserParticipant(user, pageable).map(ccs::convertToDto);
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
    @Transactional
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

        return ccs.convertToDto(conv);
    }

    @Override
    @Transactional
    public GetMessageDto sendNewMessage(Users user, SendMessageDto dto) {
        Conversation currentConv = convRepo.findById(dto.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        Participant sender = currentConv.getParticipants().stream()
                .filter(part -> Objects.equals(part.getUser().getUserId(), user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ConversationIntegrityException(
                        "Conversation with ID: " + currentConv.getConversationId() +
                                " is missing a purported sender participant with ID: " + user.getUserId()));

        if(sender.isArchived() || sender.isMuting()) {
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

        Message newMsg = new Message(currentConv, sender, repliedToMessage, dto);

        messageRepository.save(newMsg);

        currentConv.setLastMsg(newMsg);
        convRepo.save(currentConv);

        return msgConvSrv.convertToDto(newMsg);
    }

    private Conversation generateNewConversation(Users user,
                                                 FindOrStartNewConversationDto dto,
                                                 String dmKey) {
        Conversation newConv = new Conversation(user, dto, dmKey);

        Participant sender = new Participant(user, newConv, ConversationParticipantRole.MEMBER);
        Participant recipient = new Participant(user, newConv, ConversationParticipantRole.MEMBER);

        participantRepo.save(sender);
        participantRepo.save(recipient);

        return convRepo.save(newConv);
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

        if (sender.isMuting() || sender.isArchived()) {
            throw new ConfirmationRequiredException(
                    "You muted or archived this conversation. Undo to resume?");
        }

        sender.setLastActiveAt(Instant.now());
        participantRepo.save(sender);
    }
}
