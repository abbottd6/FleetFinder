package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="push_subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscription {

    public PushSubscription(Users user, CreatePushSubRequestDto dto) {
        this.user = user;
        this.userLabel = dto.getUserLabel();
        this.deviceUrl = dto.getDeviceUrl();
        this.publicKey = dto.getPublicKey();
        this.browserSecret = dto.getBrowserSecret();
        this.sysNotesEnabled = false;
        this.groupNotesEnabled = true;
        this.socialNotesEnabled = true;
        this.dailyFailureCount = 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_push_sub")
    private Long idPushSub;

    @ManyToOne
    @NotNull(message="PushSubscription entity field 'User' cannot be null.")
    @JoinColumn(name="user_id")
    private Users user;

    @Column(name="user_label")
    @NotNull(message="PushSubscription entity field 'userLabel' cannot be null.")
    @Size(min = 2, max = 32, message = "PushSubscription entity field 'userLabel' must be between 2 and 32 characters.")
    private String userLabel;

    @Column(name="device_url")
    @NotBlank(message="PushSubscription entity field 'deviceUrl' cannot be blank or null.")
    private String deviceUrl;

    @Column(name="public_key")
    @NotBlank(message="PushSubscription entity field 'publicKey' cannot be blank or null.")
    private String publicKey;

    @Column(name="browser_secret")
    @NotBlank(message="PushSubscription entity field 'browserSecret' cannot be blank or null.")
    private String browserSecret;

    @Column(name="sys_notes_enabled")
    @NotNull(message="PushSubscription entity field 'sysNotesEnabled' cannot be null.")
    private Boolean sysNotesEnabled;

    @Column(name="group_notes_enabled")
    @NotNull(message="PushSubscription entity field 'groupNotesEnabled' cannot be null.")
    private Boolean groupNotesEnabled;

    @Column(name="social_notes_enabled")
    @NotNull(message="PushSubscription entity field 'socialNotesEnabled' cannot be null.")
    private Boolean socialNotesEnabled;

    @Column(name="daily_failure_count")
    @NotNull(message="PushSubscription entity field 'dailyFailureCount' cannot be null.")
    private Integer dailyFailureCount;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
