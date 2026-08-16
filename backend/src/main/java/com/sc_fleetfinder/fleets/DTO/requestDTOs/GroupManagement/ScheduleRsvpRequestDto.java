package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ScheduleRsvpRequestDto {

    @NotNull(message="ScheduleRsvpRequestDto field 'listingId' cannot be null")
    private Long listingId;
    private Long subgroupId;
    private Instant scheduledTime;
    private Instant expiresAt;
    private String rsvpMessage;
    private String commsShare;
}
