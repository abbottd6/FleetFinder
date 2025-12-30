package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.chat.ConversationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionService;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantRole;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.sc_fleetfinder.fleets.utils.DmKeyUtil.sha256DmKey;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ConversationConversionService ccs;
    private final ParticipantRepository participantRepo;
    private final MessageRepository msgRepo;
    private final MessageConversionService msgConvSrv;
    private final ConversationRepository convRepo;

    ChatServiceImpl(ConversationConversionService ccs,
                    ParticipantRepository participantRepo,
                    MessageRepository msgRepo,
                    MessageConversionService msgConvSrv,
                    ConversationRepository convRepo) {
        this.ccs = ccs;
        this.msgRepo = msgRepo;
        this.msgConvSrv = msgConvSrv;
        this.participantRepo = participantRepo;
        this.convRepo = convRepo;
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
            if(participantRepo.findByConversationAndUser(user.getUserId(), convId).isPresent()) {
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
    public ResponseEntity<GetConversationDto> findOrStartNew(Users user, FindOrStartNewConversationDto dto) {
        String dmKey = sha256DmKey(user.getUserId(), dto.getRecipientId());

        Conversation conv = convRepo.findByDmKey(dmKey)
                .map(existing -> {
                    verifyParticipantsOrThrow(user.getUserId(), dto.getRecipientId(), existing);
                    if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
                    return existing;
                })
                .orElseGet(() -> generateNewConversation(user, dto, dmKey));

        convRepo.save(conv);

        return ResponseEntity.ok(ccs.convertToDto(conv));
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

    private void verifyParticipantsOrThrow(Long userAId, Long userBId, Conversation conv) {
        Participant sender = participantRepo.findByConversationAndUser(userAId, conv.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ConversationParticipant", userAId, conv.getConversationId()));

        participantRepo.findByConversationAndUser(userBId, conv.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ConversationParticipant", userBId, conv.getConversationId()));

        if (sender.isMuting() || sender.isArchived()) {
            throw new ConfirmationRequiredException(
                    "You muted or archived this conversation. Undo to resume?");

        }

        sender.setLastActiveAt(Instant.now());
        participantRepo.save(sender);
    }
}
