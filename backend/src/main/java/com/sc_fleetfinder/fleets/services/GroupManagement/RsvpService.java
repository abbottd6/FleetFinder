package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.WrapperDtoRsvpActiveMastersResponse;
import com.sc_fleetfinder.fleets.entities.Users;

public interface RsvpService {

    WrapperDtoRsvpActiveMastersResponse getRsvpActiveMastersList(Users manager, Long listingId);
}
