package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController() {};

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public List<PublicUserResponseDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PublicUserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PrivateUserResponseDto getMe(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        return userService.getUserByKeycloakId(kcId);
    }

    @PostMapping("/create-user")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PrivateUserResponseDto createUser(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        return userService.createUser(kcId, username, email);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    // needs to use authentication principal and keycloakId instead of userId
    public PrivateUserResponseDto updateUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserDto updateUserDto) {
        String kcId = jwt.getSubject();

        return userService.updateUser(kcId, updateUserDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    // needs to use authentication principal and keycloakId instead of userId
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        userService.deleteUser(kcId);
        return ResponseEntity.noContent().build();
    }
}
