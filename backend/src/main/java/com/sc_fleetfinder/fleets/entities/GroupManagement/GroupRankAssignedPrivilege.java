package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.AssignedPrivilegeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="group_rank_assigned_privilege")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupRankAssignedPrivilege {

    @EmbeddedId
    private AssignedPrivilegeId rankAssignedPrivilegeId;

    @MapsId
    @ManyToOne
    @JoinColumn(name="rank_id", referencedColumnName="id_rank")
    private InGroupRank assignedToRank;

    @MapsId
    @ManyToOne
    @JoinColumn(name="privilege_id", referencedColumnName="id_privilege")
    private RankPrivilegeType privilegeType;
}
