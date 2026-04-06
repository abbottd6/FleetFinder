package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRosterClass;
import lombok.Data;

@Data
public class SendGroupInviteRequestDto {

    private Long listingId;
    private GroupRosterClass rosterClass;
    private String requestMessage;
}
