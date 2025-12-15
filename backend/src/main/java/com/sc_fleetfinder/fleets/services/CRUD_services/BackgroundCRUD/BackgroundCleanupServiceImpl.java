package com.sc_fleetfinder.fleets.services.CRUD_services.BackgroundCRUD;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class BackgroundCleanupServiceImpl implements BackgroundCleanupService {

    private final GroupListingRepository glr;

    public BackgroundCleanupServiceImpl(GroupListingRepository glr) {
        this.glr = glr;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> clickedListingsClean(Set<Long> clickedIds) {
        Objects.requireNonNull(clickedIds);
        Map<String, Set<Long>> response = new HashMap<>();

        try {
            if(clickedIds.isEmpty()) {
                response.put("cleaned", Set.of());
                return ResponseEntity.ok(response);
            }

            Set<Long> cleanedIds = glr.findAllIds(clickedIds);

            response.put("cleaned", cleanedIds);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("clickedListingsClean throwing error: {}", e.getMessage());
            response.put("cleaned", new HashSet<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
