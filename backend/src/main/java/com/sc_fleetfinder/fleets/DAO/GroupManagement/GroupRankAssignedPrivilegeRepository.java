package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupRankAssignedPrivilege;
import com.sc_fleetfinder.fleets.utils.GroupManagement.AssignedPrivilegeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRankAssignedPrivilegeRepository extends JpaRepository<GroupRankAssignedPrivilege, AssignedPrivilegeId> {
}
