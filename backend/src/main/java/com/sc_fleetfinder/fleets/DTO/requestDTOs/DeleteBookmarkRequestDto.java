package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteBookmarkRequestDto {

    private Long bookmarkId;

    @NotNull(message = "DeleteBookmarkRequestDto field 'groupId' cannot be null.")
    private Long groupId;

    private Users user;
}
