package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPositionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewPositionTemplateRepository extends JpaRepository<CrewPositionTemplate, Long> {

    @Query("""
           SELECT p FROM CrewPositionTemplate p
           WHERE p.templateRootId = :templateId
           ORDER BY p.sortOrder ASC
           """)
    List<CrewPositionTemplate> findByTemplateRootId(@Param("templateId") Long templateId);
}
