package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface HiddenListingService {

    Set<Long> getMyHiddenListingsBrief(Users user);

    ResponseEntity<?> userAddHidden(Users user, Long groupId);

    ResponseEntity<?> userUndoLastHide(Users user);

    ResponseEntity<?> userClearHidden(Users user);
}
