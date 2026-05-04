package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewSubgroupTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewSubgroupTemplateRepository extends JpaRepository<CrewSubgroupTemplate, Long> {

    @Query("""
           SELECT s FROM CrewSubgroupTemplate s
           WHERE s.templateId = :templateId
           ORDER BY s.sortOrder ASC
           """)
    List<CrewSubgroupTemplate> findByTemplateId(@Param("templateId") Long templateId);
}
