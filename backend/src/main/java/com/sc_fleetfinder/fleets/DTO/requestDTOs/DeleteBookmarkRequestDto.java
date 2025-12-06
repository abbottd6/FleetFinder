package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteBookmarkRequestDto {

    private Long bookmarkId;

    @NotNull(message = "DeleteBookmarkRequestDto field 'groupId' cannot be null.")
    private Long groupId;

    // ##TODO should not get userId from frontend or have it as part of the dto, should be derived from token and passed separately
    private Users user;
}
