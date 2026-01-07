package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationUnreadCountDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    NotificationServiceImpl(NotificationRepository notificationRepo,
                            ModelMapper modelMapper,
                            SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.notificationRepo = notificationRepo;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public Page<GetNotificationDto> getMyNotifications(Users user, Pageable pageable) {
        Page<Notification> myNotes = notificationRepo.findAllByUserId(user.getUserId(), pageable);

        return myNotes.map(note -> modelMapper.map(note , GetNotificationDto.class));
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
}
