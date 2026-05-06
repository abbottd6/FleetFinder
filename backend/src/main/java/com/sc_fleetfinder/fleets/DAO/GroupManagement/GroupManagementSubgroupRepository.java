package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupManagementSubgroupRepository extends JpaRepository<GroupManagementSubgroup, Long> {

    @Query("""
           SELECT sub FROM GroupManagementSubgroup sub
           WHERE (sub.subgroupId IN :rootIds
                OR sub.rootSubgroupId IN :rootIds)
                AND sub.groupListing.groupId = :groupId
           ORDER BY sub.sortOrder,
                    sub.createdAt
           """)
    List<GroupManagementSubgroup> findTreeByRoot(@Param("rootIds") List<Long> rootIds,
                                                 @Param("groupId") Long groupId);

    @Query(value = """
           SELECT sub.id_subgroup FROM group_management_subgroup sub
           WHERE sub.listing_id = :groupId
                AND sub.root_subgroup_id = sub.id_subgroup
           ORDER BY sub.sort_order,
                    sub.created_at
           """, nativeQuery = true)
    List<Long> findGroupCompositionRootIds(@Param("groupId") Long groupId);
}
