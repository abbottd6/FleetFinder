package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;

public interface UserConversionService {

    PrivateUserResponseDto convertToPrivateDto(Users user);

    PublicUserResponseDto convertToPublicDto(Users user);
}
