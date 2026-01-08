package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.services.CRUD_services.BackgroundCRUD.BackgroundCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/be-busy")
@Slf4j
public class BackgroundCleanupController {

    private final BackgroundCleanupService cleanupService;

    BackgroundCleanupController(BackgroundCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @PutMapping("/clicked-clean")
    public ResponseEntity<?> clickedListingsClean(@RequestBody Set<Long> clickedIds) {

        return cleanupService.clickedListingsClean(clickedIds);
    }
}
