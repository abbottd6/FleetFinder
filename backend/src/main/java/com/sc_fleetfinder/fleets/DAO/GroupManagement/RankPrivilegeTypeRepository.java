package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.RankPrivilegeType;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankPrivilegeTypeRepository extends JpaRepository<RankPrivilegeType, RankPrivilegeOptions> {
}
