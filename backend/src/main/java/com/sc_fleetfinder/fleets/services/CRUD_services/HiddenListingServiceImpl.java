package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.HiddenListingRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.HiddenListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HiddenListingServiceImpl implements HiddenListingService {

    private final HiddenListingRepository hlr;
    private final GroupListingRepository glr;

    public HiddenListingServiceImpl(HiddenListingRepository hlr, GroupListingRepository glr) {
        this.hlr = hlr;
        this.glr = glr;
    }

    @Override
    public Set<Long> getMyHiddenListingsBrief(Users user) {
         return hlr.findByUser(user).stream()
                .map(hidden -> hidden.getListing().getGroupId())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public ResponseEntity<?> userAddHidden(Users user, Long groupId) {
        Map<String, String> response = new HashMap<>();

        try {
            GroupListing listing = glr.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupListing", groupId));

            if(hlr.findByUserAndListing(user, listing).isPresent()) {
                response.put("Response", "Already Hidden");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            HiddenListing hide = new HiddenListing(user, listing);

            hlr.save(hide);

            response.put("Response", "Listing Hidden");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (ResourceNotFoundException e) {

            response.put("Response", "Unable to hide this listing" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> userUndoLastHide(Users user) {
        Map<String, String> response = new HashMap<>();

        Optional<GroupListing> unHidden = hlr.findTopByUserOrderByHiddenAtDesc(user)
                .map(hidden -> {
                    GroupListing listing = hidden.getListing();
                    hlr.delete(hidden);
                    return listing;
                });

        if(unHidden.isPresent()) {
            GroupListing temp = unHidden.get();
            String title = temp.getListingTitle();
            response.put("Response", (title + " has been unhidden.").length() >= 20 ? title : title.substring(0,20) + "... has been unhidden.");
        } else {
            response.put("Response", "No hidden listings");
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<?> userClearHidden(Users user) {
        try {
            hlr.deleteAllByUser(user);

            Map<String, String> response = new HashMap<>();
            response.put("Response", "Hidden listings cleared.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
