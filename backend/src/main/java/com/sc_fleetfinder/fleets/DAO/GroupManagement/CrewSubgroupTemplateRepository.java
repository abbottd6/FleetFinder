package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewSubgroupTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewSubgroupTemplateRepository extends JpaRepository<CrewSubgroupTemplate, Long> {

    List<CrewSubgroupTemplate> findByTemplate(CrewTemplate template);
}
