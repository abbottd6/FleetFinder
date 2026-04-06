package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewTemplateRepository extends JpaRepository<CrewTemplate, Long> {
}
