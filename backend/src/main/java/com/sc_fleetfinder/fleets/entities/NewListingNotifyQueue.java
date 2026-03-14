package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name = "new_listing_notify_queue")
@Getter
@Setter
public class NewListingNotifyQueue {

    @Id
    @Column(name = "id_group")
    private Long groupId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_group")
    private GroupListing groupListing;

    @Column(name = "status")
    @NotNull(message = "NewListingNotifyQueue field 'status' cannot be null.")
    private String status = "inQueue";

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "queued_at")
    private Instant queuedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "locked_at", nullable = true)
    private Instant lockedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "processed_at", nullable = true)
    private Instant processedAt;
}
