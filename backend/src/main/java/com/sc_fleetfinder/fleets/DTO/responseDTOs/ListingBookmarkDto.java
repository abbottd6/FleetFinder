package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import lombok.Data;

import java.time.Instant;

@Data
public class ListingBookmarkDto {
    private Long id;
    private GroupListingResponseDto groupListingResponseDto;
    private Instant createdAt;
}
