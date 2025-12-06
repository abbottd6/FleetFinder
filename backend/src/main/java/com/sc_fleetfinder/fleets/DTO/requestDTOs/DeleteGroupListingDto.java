package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class DeleteGroupListingDto {

    @NotNull(message = "Delete group listing request DTO field 'groupId' cannot be null.")
    private Long groupId;

    // ##TODO this probably should not include a user ID
    @NotNull(message = "Delete group listing request DTO field 'userId' cannot be null.")
    private Long userId;

    // ##TODO ?? this should not exist, it should just be the controller parameter and not need a constructor
    // ##TODO Or maybe this dto should not even exist (since user should not be in here) and the controller should just use a path variable
    // ##TODO and then verify that the listing belongs to the Authd user before deleting
    public DeleteGroupListingDto(Long userId, Long listingId) {
        this.userId = userId;
        this.groupId = listingId;
    }
}
