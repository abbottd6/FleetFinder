package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrUpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface UserService {

    List<PrivateUserResponseDto> getAllUsers();

    /**
     * Perform repository-level uniqueness checks on keycloakId, email, and username.
     * Create a new user if none exists.
     * Before saving: lowercase email and validate data to ensure no duplicate users (keycloakId, username, email)
     *
     * @param kcId sub from the User Controller Jwt
     * @param rawUsername "preferred_username" claim from keycloak
     * @param rawEmail "email" claim from keycloak
     * @return newly created userDto
     * @throws UserConflictException if email or userName is already in use by another user
     */
    @Validated
    PrivateUserResponseDto createUser(String kcId, String rawUsername, String rawEmail);


    @Validated
    PrivateUserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto);


    void deleteUser(Long id);
    PrivateUserResponseDto getUserById(Long id);
}
