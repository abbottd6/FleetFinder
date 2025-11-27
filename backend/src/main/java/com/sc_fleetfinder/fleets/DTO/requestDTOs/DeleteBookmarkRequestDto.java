package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteBookmarkRequestDto {

    @NotNull(message = "DeleteBookmarkRequestDto field 'bookmarkId' cannot be null.")
    private Long bookmarkId;

    private Long userId;

    @NotNull(message = "DeleteBookmarkRequestDto field 'groupId' cannot be null.")
    private Long groupId;
}
