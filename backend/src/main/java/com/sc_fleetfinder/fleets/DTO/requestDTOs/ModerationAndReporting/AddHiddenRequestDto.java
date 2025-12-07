package com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddHiddenRequestDto {

    @NotNull(message="AddHiddenRequestDto field 'groupId' cannot be null.")
    private Long groupId;
}
