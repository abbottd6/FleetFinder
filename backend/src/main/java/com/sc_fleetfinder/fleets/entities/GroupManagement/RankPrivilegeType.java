package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private RankPrivilegeOptions privilegeType;
}
