package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    //need tests after v2 refactor
    //and refactoring for security to make user info minimally (or not at all) accessible through api endpoints
    //getter methods for users should not return passwords and should require admin status in order to view the
    // endpoints
    @Autowired
    private UserService userService;
    private final UserRepository userRepository;

    public UserController(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    @GetMapping
    public List<PrivateUserResponseDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public PrivateUserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/me")
    public PrivateUserResponseDto createUser(@Valid @AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        Users thisUser = userRepository.findByKeycloakId(kcId)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setKeycloakId(kcId);
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    return userRepository.save(newUser);
                });

        return new PrivateUserResponseDto(thisUser.getUserId(), thisUser.getKeycloakId(), thisUser.getUsername(), thisUser.getEmail());
    }

    @PutMapping("/{id}")
    public PrivateUserResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
