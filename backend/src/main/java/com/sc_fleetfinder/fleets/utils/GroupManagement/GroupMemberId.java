package com.sc_fleetfinder.fleets.utils.GroupManagement;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberId implements Serializable {

    private Long listingId;
    private Long userId;
    private Instant createdAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof GroupMemberId that)) return false;
        return Objects.equals(listingId, that.listingId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() { return Objects.hash(listingId, userId, createdAt); }
}
