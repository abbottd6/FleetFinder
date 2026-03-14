package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import jakarta.persistence.Column;
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
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name = "user_custom_notification")
@Getter
@Setter
public class UserCustomNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_custom_note")
    private Long customNoteId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "UserCustomNotification field 'user' cannot be null.")
    private Users user;

    @Column(name = "enabled")
    @NotNull(message = "UserCustomNotification field 'enabled' cannot be null.")
    private Boolean enabled = true;

    @Column(name = "tag_label", nullable = true)
    @Size(max = 32)
    private String tagLabel;

    @ManyToOne
    @JoinColumn(name = "environment_id", nullable = true)
    private GameEnvironment environment;

    @ManyToOne
    @JoinColumn(name = "experience_id", nullable = true)
    private GameExperience experience;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private GameplayCategory category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = true)
    private GameplaySubcategory subcategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = true)
    private LanguageOptions languageCode;

    @ManyToOne
    @JoinColumn(name = "pvp_status_id", nullable = true)
    private PvpStatus pvpStatus;

    @Column(name = "keywords", nullable = true)
    @Size(max = 32)
    private String keywords;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_at")
    private Instant createdAt;
}
