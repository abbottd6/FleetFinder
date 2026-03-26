package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationUnreadCountDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
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
import com.sc_fleetfinder.fleets.messaging.push.PushNotificationService;
import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ListingArchiveRepository archiveRepo;
    private final GroupListingRepository groupListingRepository;
    private final NotificationOutboxRepository outboxRepo;
    private final MessageRepository msgRepo;
    private final PushSubscriptionRepository pushSubRepo;
    private final PushNotificationService pushNotificationService;

    @Override
    public Page<GetNotificationDto> getMyDropdownNotifications(Users user, Pageable pageable) {
        Page<Notification> myNotes = notificationRepo.findAllDropdownNotificationsByUserId(user.getUserId(), pageable);

        return myNotes.map(note -> modelMapper.map(note , GetNotificationDto.class));
    }

    @Override
    public Page<GetNotificationDto> getAllMyNotifications(Users user, Pageable pageable) {
        Page<Notification> myNotes = notificationRepo.findAllNotificationsByUserId(user.getUserId(), pageable);

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

            case NotificationType.LISTING_VIS_STATUS_CHANGED:
                Notification savedVisNote = buildListingStatusChangeNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedVisNote, outboxEntity);
                break;

            case NotificationType.LISTING_ARCHIVED:
                Notification savedArchiveNote = buildListingArchiveNotification(outboxEntity);
                identifyDeliveryChannel_andSend(savedArchiveNote, outboxEntity);
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
                break;
            case DeliveryChannel.DISCORD:
                break;
            case DeliveryChannel.PUSH:
                sendPushNotification(note, outboxEntity.getTargetPushSub());
                break;
            default:
                throw new IllegalArgumentException("DeliveryChannel: " + note.getDeliveryChannel() +
                        " for outbox notification with ID: " + note.getOutbox().getOutboxId() + " is not supported.");
        }
    }

    private void sendInAppNotification(Notification note) {
        GetNotificationDto noteDto = modelMapper.map(note, GetNotificationDto.class);

        NotificationUnreadCountDto unreadCount = new NotificationUnreadCountDto(
                notificationRepo.countUnreadByUserId(
                        note.getUser().getUserId()
                )
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

    private void sendDiscordNotification(Notification note) {
        GetNotificationDto noteDto = modelMapper.map(note, GetNotificationDto.class);
        //todo
    }

    private void sendPushNotification(Notification note, PushSubscription pushSub) {
        String payload = note.getTitle() + ". " + note.getMessage();

        try {
            pushNotificationService.sendPushNotification(pushSub, payload);
        } catch (Exception e) {
            note.getOutbox().setLastError("Failed to send push notification for push subscription with ID: "
                    + pushSub.getIdPushSub() + ", and notification outbox ID: " + note.getOutbox().getOutboxId()
                    + ", and notification ID: " + note.getNotificationId() + " \n" + e.getMessage());
            note.getOutbox().setStatus("PARTIAL_FAILURE");
            outboxRepo.save(note.getOutbox());
        }
    }

    private Notification buildNewMessageNotification(NotificationOutbox obEntity) {
        String title = "You have a new message from ";
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
                "matched a new listing:";

        String message = outboxEntity.getPayloadJson().getTargetLabel();

        Notification newNote = new Notification(outboxEntity, title, message);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey());

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildListingStatusChangeNotification(NotificationOutbox outboxEntity) {
        String title = groupListingRepository.findById(outboxEntity.getEntityId())
                .map(GroupListing::getListingTitle)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupListing", outboxEntity.getEntityId()));

        String msg = "The status of your listing '" + outboxEntity.getPayloadJson().getTargetLabel()
                + "' has changed to: " + outboxEntity.getEntityNewStatus();

        Notification newNote = new Notification(outboxEntity, title, msg);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey());

        if (readAt.isPresent()) {
            throw new SkipExternalNotificationProcessingException("Sibling notification already read.");
        }

        return notificationRepo.save(newNote);
    }

    private Notification buildListingArchiveNotification(NotificationOutbox outboxEntity) {
        String title = archiveRepo.findByGroupId(outboxEntity.getEntityId())
                .map(ListingArchive::getListingTitle)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ListingArchive", outboxEntity.getEntityId()));

        String msg = "Your listing with title: '" + outboxEntity.getPayloadJson().getTargetLabel()
                + " has been archived.";

        Notification newNote = new Notification(outboxEntity, title, msg);

        Optional<Instant> readAt = notificationRepo.checkSiblingNotificationReadStatus(
                outboxEntity.getEntityOwner().getUserId(), outboxEntity.getSiblingKey());

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
