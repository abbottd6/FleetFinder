package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ModListingActionDto {

    @NotNull(message="ModListingActionDto field 'actionId' cannot be null.")
    private Long actionId;

    private Long archiveId;

    private Long groupId;

    @NotNull(message="ModListingActionDto field 'userId' cannot be null.")
    private Long userId;

    @NotNull(message="ModListingActionDto field 'username' cannot be null.")
    private String username;

    private Long modId;

    private String modName;

    @NotNull(message="ModListingActionDto field 'actionType' cannot be null.")
    private String actionType;

    private String actionNote;

    @NotNull(message="ModListingActionDto field 'actionTs' cannot be null.")
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
    private Instant actionTs;
}
