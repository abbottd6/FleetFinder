package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="user_moderation_record")
@Getter
@Setter
public class UserModerationRecord {

    protected UserModerationRecord() {};
    public UserModerationRecord(Users user) {
        this.user = user;
        this.modActionCount = 0;
    }

    @Id
    @Column(name="id_user")
    private Long userId;

    @OneToOne(fetch= FetchType.LAZY)
    @MapsId
    @JoinColumn(name="id_user")
    private Users user;

    @Column(name="mod_action_count")
    @NotNull(message="UserModerationRecord entity field 'modActionCount' cannot be null.")
    private Integer modActionCount;

    @Column(name="last_mod_action")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastModActionTs;

    @Column(name="is_banned")
    private boolean isBanned;
}
