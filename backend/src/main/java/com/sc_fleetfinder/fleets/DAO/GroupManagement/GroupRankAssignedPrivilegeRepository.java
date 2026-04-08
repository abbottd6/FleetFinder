package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupRankAssignedPrivilege;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.GroupManagement.RankPrivilegeType;
import com.sc_fleetfinder.fleets.utils.GroupManagement.AssignedPrivilegeId;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRankAssignedPrivilegeRepository extends JpaRepository<GroupRankAssignedPrivilege, AssignedPrivilegeId> {

    @Query("""
            SELECT p.privilegeType.privilegeType FROM GroupRankAssignedPrivilege p
            WHERE p.assignedToRank = :rank
            """)
    List<RankPrivilegeOptions> getAssignedPrivilegesByRank(@Param("rank") InGroupRank rank);
}
