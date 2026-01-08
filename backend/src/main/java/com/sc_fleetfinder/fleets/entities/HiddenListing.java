package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(
        name="hidden_listing",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_group"})
)
@Getter
@Setter
public class HiddenListing {

    protected HiddenListing() {}

    public HiddenListing(Users user, GroupListing listing) {
        this.user = user;
        this.listing = listing;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hide_id")
    private Long hideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_user")
    @NotNull(message="HiddenListing field 'user' cannot be null.")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_group")
    @NotNull(message="HiddenListing field 'listing' cannot be null.")
    private GroupListing listing;

    @CreationTimestamp
    @Column(name="hidden_at", nullable = false, updatable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant hiddenAt = Instant.now();
}
