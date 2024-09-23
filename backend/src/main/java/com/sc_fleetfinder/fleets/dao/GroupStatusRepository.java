package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupStatusRepository extends JpaRepository<GroupStatus, Integer> {
}
