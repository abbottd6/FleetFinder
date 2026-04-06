package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewPositionRepository extends JpaRepository<CrewPosition, Long> {
}
