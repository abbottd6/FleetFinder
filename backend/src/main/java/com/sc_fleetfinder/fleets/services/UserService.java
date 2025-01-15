package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.UserRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.UserResponseDto;
import com.sc_fleetfinder.fleets.entities.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto createUser(@Valid UserRequestDto userRequestDto);
    UserResponseDto updateUser(@PathVariable Long id, @Valid UserRequestDto userRequestDto);
    void deleteUser(Long id);
    UserResponseDto getUserById(Long id);
}
