package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewRoleClassificationRepository extends JpaRepository<CrewRoleClassification, Long> {

    @Query("""
           SELECT role FROM CrewRoleClassification role
           WHERE role.roleCreator IS NULL OR role.roleCreator.userId = :userId
           """)
    List<CrewRoleClassification> findByUserAndGlobalClassifications(@Param("userId") Long userId);
}
