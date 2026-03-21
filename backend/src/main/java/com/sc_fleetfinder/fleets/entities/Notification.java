package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadataConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    public Notification(Users user, NotificationType type,
                        String title, String message,
                        ModListingAction modAction) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
        this.action = modAction;
    }

    public Notification(Users user,
                        NotificationType type,
                        String displayTitle,
                        String notificationOfStatus) {
        this.user = user;
        this.type = type;
        this.title = displayTitle;
        this.message = notificationOfStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_notification")
    private Long notificationId;

    @ManyToOne
    @NotNull(message="Notification field 'user' cannot be null.")
    @JoinColumn(name="id_user")
    private Users user;

    @Column(name="type")
    @NotNull(message="Notification field 'type' cannot be null.")
    private NotificationType type;

    @Column(name="title", nullable=true)
    private String title;

    @Column(name="message")
    @NotNull(message="Notification field 'message' cannot be null.")
    private String message;

    @OneToOne
    @JoinColumn(name="id_action", nullable=true)
    private ModListingAction action;

    @Column(name="dropdown_priority", nullable = false)
    private Boolean dropdownPriority = true;

    @Convert(converter = NotificationTargetMetadataConverter.class)
    @Column(name="target_metadata", nullable = true)
    private NotificationTargetMetadata targetMetadata;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="read_at", nullable = true)
    private Instant readAt;
}
