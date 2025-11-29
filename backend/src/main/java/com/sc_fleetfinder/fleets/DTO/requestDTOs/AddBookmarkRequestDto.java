package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookmarkRequestDto {

    private Users user;

    @NotNull(message = "AddBookmarkRequestDto field 'groupId' cannot be null.")
    private Long groupId;

    private GroupListing group;
}
