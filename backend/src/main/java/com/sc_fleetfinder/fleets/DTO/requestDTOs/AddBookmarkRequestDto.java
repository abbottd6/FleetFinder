package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookmarkRequestDto {

    private Long userId;

    @NotNull(message = "AddBookmarkRequestDto field 'groupId' cannot be null.")
    private Long groupId;
}
