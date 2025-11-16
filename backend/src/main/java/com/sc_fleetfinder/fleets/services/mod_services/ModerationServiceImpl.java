package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ModerationServiceImpl implements ModerationService {

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository glr;
    private final GroupListingConversionService glcs;

    public ModerationServiceImpl(GroupListingRepository groupListingRepository,
                                 GroupListingConversionService groupListingConversionService) {
        this.glr = groupListingRepository;
        this.glcs = groupListingConversionService;
    }

    @Override
    public List<GroupListingResponseDto> modGetAllGroupListings() {
        List<GroupListing> groupListings = glr.findAll();

        if(groupListings.isEmpty()) {
            log.info("No group listings found.");
        }

        return groupListings.stream()
                .map(glcs::convertListingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<?> modDeleteListing(DeleteGroupListingDto modDeleteDto) {
        try {
            GroupListing groupEntity = glr.findById(modDeleteDto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(modDeleteDto.getGroupId()));
            groupEntity.setDeleted(true);
            groupEntity.setDeletedAt(Instant.now());
            groupEntity.setDeletedBy(modDeleteDto.getUserId());
            glr.flush();
            glr.save(groupEntity);

            Map<String, String> response = new HashMap<>();
            response.put("listingTitle", groupEntity.getListingTitle());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("modDeleteGroupListing failed. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
