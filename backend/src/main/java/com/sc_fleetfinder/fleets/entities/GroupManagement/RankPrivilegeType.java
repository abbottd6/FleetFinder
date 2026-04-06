package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="rank_privilege_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RankPrivilegeType {

    @Id
    @Column(name="id_privilege")
    @Enumerated(EnumType.STRING)
    private RankPrivilegeOptions privilegeType;
}
