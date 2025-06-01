package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface UserService {

    List<PrivateUserResponseDto> getAllUsers();
    @Validated
    PrivateUserResponseDto createUser(@Valid CreateUserDto createUserDto);
    @Validated
    PrivateUserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto);
    void deleteUser(Long id);
    PrivateUserResponseDto getUserById(Long id);
}
