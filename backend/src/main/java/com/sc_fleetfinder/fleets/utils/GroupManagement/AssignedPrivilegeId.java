package com.sc_fleetfinder.fleets.utils.GroupManagement;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignedPrivilegeId implements Serializable {

    private Long rankId;
    private RankPrivilegeOptions privilegeType;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof AssignedPrivilegeId that)) return false;
        return Objects.equals(rankId, that.rankId)
                && Objects.equals(privilegeType, that.privilegeType);
    }

    @Override
    public int hashCode() { return Objects.hash(rankId, privilegeType);}
}
