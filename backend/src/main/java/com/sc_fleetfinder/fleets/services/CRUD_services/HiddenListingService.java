package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.http.ResponseEntity;

public interface HiddenListingService {

    ResponseEntity<?> getMyHiddenListingsBrief(Users user);

    ResponseEntity<?> userHideListing(Users user, Long groupId);

    ResponseEntity<?> userUndoLastHide(Users user);

    ResponseEntity<?> userClearHidden(Users user);
}
