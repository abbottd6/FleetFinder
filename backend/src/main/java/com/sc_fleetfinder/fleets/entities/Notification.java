package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import com.sc_fleetfinder.fleets.utils.ParentEntityReference;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadataConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    //LISTING ARCHIVE/STATUS CHANGE NOTIFICATION CONSTRUCTOR
    public Notification(NotificationOutbox outbox, String title, String message) {
        this.user = outbox.getEntityOwner();
        this.type = outbox.getEventType();
        this.outbox = outbox;
        this.title = title;
        this.message = message;
        this.deliveryChannel = outbox.getDeliveryChannel();
        this.parentEntity = new ParentEntityReference(outbox.getParentEntityId(),
                outbox.getParentEntityType());
        this.siblingKey = outbox.getSiblingKey();
    }

    //NEW MESSAGE NOTIFICATION CONSTRUCTOR
    public Notification(NotificationOutbox outbox,
                        String title, String message, boolean dropdownPriority) {

        this.user = outbox.getEntityOwner();
        this.outbox = outbox;
        this.type = outbox.getEventType();
        this.title = title;
        this.message = message;
        this.deliveryChannel = outbox.getDeliveryChannel();
        this.parentEntity = new ParentEntityReference(
                outbox.getParentEntityId(), outbox.getParentEntityType());
        this.targetMetadata = outbox.getPayloadJson();
        this.siblingKey = outbox.getSiblingKey();
        this.dropdownPriority = dropdownPriority;
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
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name="title", nullable=true)
    private String title;

    @Column(name="message")
    @NotNull(message="Notification field 'message' cannot be null.")
    private String message;

    @ManyToOne
    @JoinColumn(name = "outbox_id", nullable = true)
    private NotificationOutbox outbox;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_channel", nullable = false)
    @NotNull(message = "Notification field 'deliveryChannel' cannot be null.")
    private DeliveryChannel deliveryChannel = DeliveryChannel.IN_APP;

    @Embedded
    private ParentEntityReference parentEntity;

    @Column(name = "sibling_key", nullable = true)
    private String siblingKey;

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
