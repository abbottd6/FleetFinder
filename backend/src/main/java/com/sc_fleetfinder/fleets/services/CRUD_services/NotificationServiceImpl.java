package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationUnreadCountDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ListingArchiveRepository archiveRepo;
    private final GroupListingRepository groupListingRepository;

    NotificationServiceImpl(NotificationRepository notificationRepo,
                            ModelMapper modelMapper,
                            SimpMessagingTemplate messagingTemplate,
                            GroupListingRepository groupListingRepository,
                            ListingArchiveRepository archiveRepo) {
        this.notificationRepo = notificationRepo;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
        this.groupListingRepository = groupListingRepository;
        this.archiveRepo = archiveRepo;
    }

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
    public Integer deleteAllNotifications(Users user) {
        return notificationRepo.deleteAllByUser_userId(user.getUserId());
    }

    @Override
    @Transactional
    public void createAndSendDeleteNotification(ListingArchive archive,
                                                ModerationIssue issue,
                                                NotificationType type,
                                                ModListingAction action) {
        String title = archive.getListingTitle();
        String basis = issue.getMaxReportBasis();

        Notification newNote = new Notification(issue.getUserRef(),
                NotificationType.MOD_DELETE, title, basis, action);

        newNote = notificationRepo.save(newNote);

        GetNotificationDto noteDto = modelMapper.map(newNote, GetNotificationDto.class);

        NotificationUnreadCountDto unreadCount = new NotificationUnreadCountDto(
                notificationRepo.countUnreadByUserId(
                issue.getUserRef().getUserId()
                )
        );

        String recipPrincipal = issue.getUserRef().getKeycloakId();

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
    public void sendOutboxNotification(NotificationOutbox obEntity) {
        String title = "";

        if(obEntity.getEventType() == NotificationType.LISTING_VIS_STATUS_CHANGED) {
            title = groupListingRepository.findById(obEntity.getEntityId())
                    .map(GroupListing::getListingTitle)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GroupListing", obEntity.getEntityId()));
        } else if(obEntity.getEventType() == NotificationType.LISTING_ARCHIVED) {
            title = archiveRepo.findByGroupId(obEntity.getEntityId())
                    .map(ListingArchive::getListingTitle)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ListingArchive", obEntity.getEntityId()));
        }

        Notification newNote = new Notification(
                obEntity.getEntityOwner(),
                obEntity.getEventType(),
                title,
                obEntity.getEntityNewStatus());

        notificationRepo.save(newNote);

        GetNotificationDto noteDto = modelMapper.map(newNote, GetNotificationDto.class);

        NotificationUnreadCountDto unreadCount = new NotificationUnreadCountDto(
                notificationRepo.countUnreadByUserId(
                        obEntity.getEntityOwner().getUserId()
                )
        );

        String recipPrincipal = obEntity.getEntityOwner().getKeycloakId();

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
