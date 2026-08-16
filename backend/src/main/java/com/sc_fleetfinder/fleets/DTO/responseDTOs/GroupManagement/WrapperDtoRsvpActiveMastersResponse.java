package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WrapperDtoRsvpActiveMastersResponse {
    private Boolean hasGlobalMaster;
    private List<RsvpMasterResponseDto> mastersList;
}
