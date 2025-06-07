package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserFail_ExistingKeycloakId() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("mockEmail");
        mockUser.setIsDeleted(false);

        //when a request is made to create user with the same keycloak id
        when(userRepository.findByKeycloakId("mockKeycloakId")).thenReturn(Optional.of(mockUser));

        //then
        assertAll("create Users fail assertion set: ExistingKeycloakId",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("mockKeycloakId", "none", "none")),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("User Creation failed due to pre-existing Keycloak ID:"))),
                () -> verify(userRepository, times(1)).findByKeycloakId("mockKeycloakId"));
    }

    @Test
    void createUserFail_ExistingEmail() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("mockEmail");
        mockUser.setIsDeleted(false);

        //when a create user request attempts to create a user with the same email
        when(userRepository.findByEmail("mockemail")).thenReturn(Optional.of(mockUser));

        //then
        assertAll("create Users fail assertion set: ExistingEmail",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("none", "none", "mockEmail")),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("User creation requested for existing email: "))),
                () -> verify(userRepository, times(1)).findByEmail("mockemail"));
    }

    @Test
    void createUserFail_ExistingUsername() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("mockEmail");
        mockUser.setIsDeleted(false);

        //when a create user request attempts to create a user with the same username
        when(userRepository.findByUsernameIgnoreCase("mockUsername")).thenReturn(Optional.of(mockUser));

        assertAll("create Users fail assertion set: ExistingUsername",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("none", "mockUsername", "none")),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("User creation requested for existing username: "))),
                () -> verify(userRepository, times(1)).findByUsernameIgnoreCase("mockUsername"));
    }
}
