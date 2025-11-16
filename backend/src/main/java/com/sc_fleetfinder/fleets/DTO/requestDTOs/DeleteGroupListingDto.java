package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class DeleteGroupListingDto {

    @NotNull(message = "Delete group listing request DTO field 'groupId' cannot be null.")
    private Long groupId;

    @NotNull(message = "Delete group listing request DTO field 'userId' cannot be null.")
    private Long userId;

    public DeleteGroupListingDto(Long userId, Long listingId) {
        this.userId = userId;
        this.groupId = listingId;
    }
}
