package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    @Validated
    UserResponseDto createUser(@Valid CreateUserDto createUserDto);
    @Validated
    UserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto);
    void deleteUser(Long id);
    UserResponseDto getUserById(Long id);
}
