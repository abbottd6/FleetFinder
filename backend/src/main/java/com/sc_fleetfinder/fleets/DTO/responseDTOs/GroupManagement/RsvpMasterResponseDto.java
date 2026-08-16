package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RsvpMasterResponseDto {
    private Long idRsvpMaster;
    private Long listingId;
    private Long subgroupId;
    private Long subgroupLabel;
    private Instant scheduledTime;
    private Instant expiresAt;
    private String rsvpMessage;
    private String commsShare;
    private Boolean batchedAndSent;
    private Instant createdAt;
}
