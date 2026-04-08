package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationUnreadCountDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.config.discord.DiscordBotService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.SkipExternalNotificationProcessingException;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationAction;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationDataField;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationPayload;
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationService;
import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import com.sc_fleetfinder.fleets.utils.ExternalNotifcationResult;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ListingArchiveRepository archiveRepo;
    private final ModListingActionRepository modActionRepo;
    private final GroupListingRepository groupListingRepository;
    private final NotificationOutboxRepository outboxRepo;
    private final MessageRepository msgRepo;
    private final PushNotificationService pushNotificationService;
    private final DiscordBotService discordBotService;

    @Override
    public Page<GetNotificationDto> getMyDropdownNotifications(Users user, Pageable pageable) {
        Page<Notification> myNotes = notificationRepo.findAllDropdownNotificationsByUserId(user.getUserId(), pageable);

        return myNotes.map(note -> modelMapper.map(note , GetNotificationDto.class));
    }

    @Override
    public Page<GetNotificationDto> getAllMyNotifications(Users user, Pageable pageable) {
        Page<Notification> myNotes = notificationRepo.findAllInAppNotificationsByUserId(user.getUserId(), pageable);

        return myNotes.map(note -> modelMapper.map(note , GetNotificationDto.class));
    }

    @Override
    @Transactional
    public void removeDropdownPriority(Users user, Long noteId) {
        Notification note = notificationRepo.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if(!Objects.equals(note.getUser().getUserId(), user.getUserId())) {
            throw new ActionNotAuthorizedException(
                    user.getUserId(), "remove dropdown priority", "Notification", note.getNotificationId());
        }

        note.setDropdownPriority(false);
        notificationRepo.save(note);
    }

    @Override
    @Transactional
    public void deleteNotification(Users user, Long noteId) {
        Notification note = notificationRepo.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", noteId));

        if(!Objects.equals(note.getUser().getUserId(), user.getUserId())) {
            throw new ActionNotAuthorizedException(
                    user.getUserId(), "deletion", "Notification", note.getNotificationId());
        }

        notificationRepo.deleteById(noteId);
    }

    @Override
    @Transactional
    public Integer deleteAllNotifications(Users user) {
        return notificationRepo.deleteAllByUser_userId(user.getUserId());
    }

    @Override
    @Transactional
    public void generateOutboxNotificationForModAction(ModListingAction action) {
        notificationRepo.generateOutboxNotificationsOnModListingDelete(action.getActionId());
    }

    @Override
    @Transactional
    public Integer updateRead(Users user, ReceiveReadNotesDto readDto) {
        return notificationRepo.markAsRead(user.getUserId(), readDto.readIds());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countUnread(Long userId) {
        NotificationType excluded = NotificationType.NEW_CHAT_MESSAGE;

        return notificationRepo.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void prepareAndSendOutboxNotification(NotificationOutbox outboxEntity) {

        switch (outboxEntity.getEventType()) {

            case NotificationType.NEW_CHAT_MESSAGE:
                Notification savedMsgNote = buildNewMessageNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedMsgNote, outboxEntity);
                break;

            case NotificationType.NEW_LISTING_MATCH:
                Notification savedMatchNote = buildNewListingMatchNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedMatchNote, outboxEntity);
                break;

            case NotificationType.NEW_GROUP_INVITE:
                Notification savedInviteNote = buildNewGroupInviteNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedInviteNote, outboxEntity);
                break;

            case NotificationType.LISTING_VIS_STATUS_CHANGED:
                Notification savedVisNote = buildListingStatusChangeNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedVisNote, outboxEntity);
                break;

            case NotificationType.LISTING_ARCHIVED:
                Notification savedArchiveNote = buildListingArchiveNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedArchiveNote, outboxEntity);
                break;

            case NotificationType.MOD_DELETE:
                Notification savedModActionNote = buildModDeleteNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedModActionNote, outboxEntity);
                break;

            default:
                log.debug("Attempted to process an outbox notification with an unmatched notification type." +
                        " obEntityId: {} obEntity: {}", outboxEntity.getEntityId(), outboxEntity.getEntityType());
                throw new IllegalArgumentException("Attempted to process an outbox notification with an unmatched " +
                        "notification type. obEntityId: " + outboxEntity.getEntityId() +
                        "obEntity: " + outboxEntity.getEntityType());
        }
    }


    private void identifyDeliveryChannel_andSend(Notification note, NotificationOutbox outboxEntity) {
        switch (note.getDeliveryChannel()) {
            case DeliveryChannel.IN_APP:
                sendInAppNotification(note);
                outboxEntity.setSentAt(Instant.now());
                break;
            case DeliveryChannel.DISCORD:
                String discordUserId = note.getUser().getDiscordId();
                discordBotService.sendDiscordNotification(discordUserId, note);
                outboxEntity.setSentAt(Instant.now());
                break;
            case DeliveryChannel.PUSH:
                sendPushNotification(note, outboxEntity.getTargetPushSub());
                outboxEntity.setSentAt(Instant.now());
                break;
            default:
                throw new IllegalArgumentException("DeliveryChannel: " + note.getDeliveryChannel() +
                        " for outbox notification with ID: " + note.getOutbox().getOutboxId() + " is not supported.");
        }
    }

    private void sendInAppNotification(Notification note) {
        GetNotificationDto noteDto = modelMapper.map(note, GetNotificationDto.class);

        NotificationUnreadCountDto unreadCount = new NotificationUnreadCountDto(
                notificationRepo.countUnreadByUserId(note.getUser().getUserId())
        );

        String recipPrincipal = note.getUser().getKeycloakId();

        messagingTemplate.convertAndSendToUser(
                recipPrincipal,
                "/queue/system.notify_count",
                unreadCount
        );

        messagingTemplate.convertAndSendToUser(
                recipPrincipal,
                "/queue/system.notify",
                noteDto
        );
    }

    private void sendPushNotification(Notification note, PushSubscription pushSub) {
        PushNotificationPayload payload = new PushNotificationPayload();

        payload.setTitle(note.getTitle());
        payload.setBody(note.getMessage());

        PushNotificationAction action1 = new PushNotificationAction();
        PushNotificationAction action2 = new PushNotificationAction();
        action1.setAction("view");
        action1.setTitle("View");
        action2.setAction("dismiss");
        action2.setTitle("Dismiss");
        List<PushNotificationAction> actions = List.of(action1, action2);

        payload.setActions(actions);
        payload.setRequireInteraction(false);

        PushNotificationDataField dataField = new PushNotificationDataField();

        switch (note.getType()) {
            case NotificationType.NEW_LISTING_MATCH:
                Long id = note.getTargetMetadata().getTargetId();

                payload.setTag("New Listing Match");
                dataField.setUrl("https://scfleetfinder.com/listing-details/" + id);
                break;
            case NotificationType.NEW_CHAT_MESSAGE:
                payload.setTag("New Chat Message");
                dataField.setUrl("https://scfleetfinder.com/user-account");
                break;
            case NotificationType.NEW_GROUP_INVITE:
                payload.setTag("New Group Request");
                dataField.setUrl("https://scfleetfinder.com/user-account");
            case NotificationType.MOD_DELETE:
                payload.setTag("Mod Action");
                dataField.setUrl("https://scfleetfinder.com/user-account");
                break;
            default:
                payload.setTag("Listing Status Change");
                dataField.setUrl("https://scfleetfinder.com/user-account");
                break;
        }

        payload.setData(dataField);

        try {
            Map<ExternalNotifcationResult, HttpStatus> pushResult = pushNotificationService.sendPushNotificationObject(pushSub, payload);

            if (pushResult.containsKey(ExternalNotifcationResult.SUCCESS)) {
                note.setReadAt(Instant.now());
                notificationRepo.save(note);
            } else {
                throw new ResponseStatusException(pushResult.get(ExternalNotifcationResult.FAILURE));
            }
        } catch (Exception e) {
            String error = "Failed to send push notification for push subscription with ID: "
                    + pushSub.getIdPushSub() + ". ERROR:" + e.getMessage();
            notificationRepo.delete(note);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
        }
    }

    // SEND DISCORD NOTIFICATION is in config/discord/DiscordBotService

    private Notification buildNewMessageNotification(NotificationOutbox obEntity) {
        String title = "New message from ";
        Message message = msgRepo.findMessageByConversationIdAndMessageId(
                obEntity.getParentEntityId(), obEntity.getEntityId())
                .orElse(null);

        if(message == null) {
            throw new ResourceNotFoundException("Message not found");
        }

        // looks up the notification recipient's conversation participant entry to find their last read message and
        // determine whether this new message is still unread.
        boolean isRead = msgRepo.findRecipientLastReadByConvIdAndUserId(
                obEntity.getParentEntityId(), obEntity.getEntityOwner().getUserId())
                .map(lastReadId -> lastReadId >= message.getMsgId())
                .orElse(false);

        if(isRead) {
            throw new SkipExternalNotificationProcessingException("Message was already read.");
        }

        title = title + msgRepo.findMessageByConversationIdAndMessageId(obEntity.getParentEntityId(), obEntity.getEntityId())
                .map(msg -> msg.getSender().getUsername())
                .orElse("Unknown");

        String noteMsg = obEntity.getPayloadJson().getTargetLabel();

        Notification newNote = new Notification(obEntity, title, noteMsg, false);

        return notificationRepo.save(newNote);
    }

    private Notification buildNewListingMatchNotification(NotificationOutbox outboxEntity) {
        String title = "Your custom notification '" + outboxEntity.getPayloadJson().getNoteTopic() + "' " +
                "matched a new listing";

        String message = outboxEntity.getPayloadJson().getTargetLabel();

        Notification newNote = new Notification(outboxEntity, title, message);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey())
                .map(ldt -> ldt.toInstant(ZoneOffset.UTC));

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildNewGroupInviteNotification(NotificationOutbox outboxEntity) {
        String title = "Someone as sent you a group request.";

        if(Objects.equals(outboxEntity.getEntityNewStatus(), InviteDirection.REQUEST.toString())) {
            title = "'" + outboxEntity.getPayloadJson().getNoteTopic() + "'" + " would like to join your group.";
        } else if(Objects.equals(outboxEntity.getEntityNewStatus(), InviteDirection.OFFER.toString())) {
            title = "'" + outboxEntity.getPayloadJson().getNoteTopic() + "'" + " would like you to join their group.";
        }
        String message = outboxEntity.getPayloadJson().getAddContext();

        Notification newNote = new Notification(outboxEntity, title, message);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                        outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey())
                .map(ldt -> ldt.toInstant(ZoneOffset.UTC));

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildListingStatusChangeNotification(NotificationOutbox outboxEntity) {
        String title = "The status of one of your listings has changed to " +
                outboxEntity.getEntityNewStatus();

        String msg = groupListingRepository.findById(outboxEntity.getEntityId())
                .map(GroupListing::getListingTitle)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupListing", outboxEntity.getEntityId()));

        Notification newNote = new Notification(outboxEntity, title, msg);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey())
                .map(ldt -> ldt.toInstant(ZoneOffset.UTC));

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildListingArchiveNotification(NotificationOutbox outboxEntity) {
        String msg = archiveRepo.findByGroupId(outboxEntity.getEntityId())
                .map(ListingArchive::getListingTitle)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ListingArchive", outboxEntity.getEntityId()));

        String title = "One of your listing has expired and been archived.";

        Notification newNote = new Notification(outboxEntity, title, msg);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey())
                .map(ldt -> ldt.toInstant(ZoneOffset.UTC));

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildModDeleteNotification(NotificationOutbox outboxEntity) {
        String title = "One of your listings was removed by a moderator";
        String msg = modActionRepo.findById(outboxEntity.getParentEntityId())
                .map(ModListingAction::getActionBasis)
                .map(ListingReportBasis::getBasisLabel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ModerationIssue", outboxEntity.getParentEntityId()));

        msg = "The basis for this action was: " + msg;

        Notification newNote = new Notification(outboxEntity, title, msg);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey())
                .map(ldt -> ldt.toInstant(ZoneOffset.UTC));

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserAccountDeleted(UserAccountDeleteEvent event) {
        try {
            notificationRepo.deleteAllByUser_userId(event.getDeletedUser().getUserId());
        }
        catch (Exception e) {
            log.error("User account delete event threw an error trying to delete the users " +
                    "notifications:\n{}", e.getMessage());
        }
    }
}
