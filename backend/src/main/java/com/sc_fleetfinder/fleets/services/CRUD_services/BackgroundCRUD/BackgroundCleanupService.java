package com.sc_fleetfinder.fleets.services.CRUD_services.BackgroundCRUD;


import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface BackgroundCleanupService {

    ResponseEntity<?> clickedListingsClean(Set<Long> clickedListingIds);
}
