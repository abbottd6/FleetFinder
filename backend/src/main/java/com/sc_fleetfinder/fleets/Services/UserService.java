package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.UserDto;
import com.sc_fleetfinder.fleets.entities.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    User createUser(@Valid UserDto userDto);
    User updateUser(Long id, @Valid UserDto userDto);
    void deleteUser(Long id);
    UserDto getUserById(Long id);
}
