package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.UserResponseDto;
import com.sc_fleetfinder.fleets.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    //need tests after v2 refactor
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
