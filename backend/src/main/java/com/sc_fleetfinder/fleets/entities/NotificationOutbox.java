package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadataConverter;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

@Entity
@Table(name="notification_outbox")
@Getter
@Setter
public class NotificationOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="outbox_id")
    private Long outboxId;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type")
    @NotNull(message="NotificationOutbox field 'eventType' cannot be null.")
    private NotificationType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name="delivery_channel")
    @NotNull(message="NotificationOutbox entity field 'delivery_channel' cannot be null.")
    private DeliveryChannel deliveryChannel;

    //name of entity type
    @Column(name="entity_type")
    @NotNull(message="NotificationOutbox field 'entityType' cannot be null.")
    private String entityType;

    //the entity's id (e.g. groupId)
    @Column(name="entity_id")
    @NotNull(message="NotificationOutbox field 'entityId' cannot be null.")
    private Long entityId;

    @ManyToOne
    @JoinColumn(name="entity_owner_id")
    @NotNull(message="NotificationOutbox field 'entityOwner' cannot be null.")
    private Users entityOwner;

    //specific to listing vis status updates
    @Column(name="entity_new_status", nullable = true)
    private String entityNewStatus;

    @Column(name="parent_entity_id")
    private Long parentEntityId;

    @Column(name="parent_entity_type")
    private String parentEntityType;

    @Column(name = "sibling_key", nullable = true)
    private String siblingKey;

    @Convert(converter = NotificationTargetMetadataConverter.class)
    @Column(name="payload_json", nullable = true)
    private NotificationTargetMetadata payloadJson;

    @Column(name="status")
    @NotNull(message="NotificationOutbox field 'status' cannot be null.")
    private String status;

    @ManyToOne
    @JoinColumn(name="push_sub_id", nullable = true)
    private PushSubscription targetPushSub;

    @Column(name="attempt_count")
    @NotNull(message="NotificationOutbox field 'attemptCount' cannot be null.")
    private Integer attemptCount = 0;

    @Column(name="last_error", nullable = true, columnDefinition = "TEXT")
    private String lastError;

    @Column(name="error_count")
    @NotNull(message="NotificationOutbox entity field 'errorCount' should not be null.")
    private Integer errorCount = 0;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="locked_at", nullable = true)
    private Instant lockedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="sent_at", nullable = true)
    private Instant sentAt;
}
