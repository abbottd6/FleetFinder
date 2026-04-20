package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD.ServerRegionServiceImpl;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserServiceImpl;
import com.sc_fleetfinder.fleets.services.Keycloak_Services.KeycloakAdminServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.UserConversionServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper userModelMapper;

    @Mock
    private GroupListingRepository groupListingRepository;

    @Mock
    private ServerRegionRepository serverRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private ServerRegionServiceImpl serverService;

    @Mock
    private KeycloakAdminServiceImpl kcAdminService;

    @Mock
    private UserConversionServiceImpl userConversionService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private static ApplicationEventPublisher eventPublisher;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl(userRepository, userConversionService, validator,
                groupListingRepository, kcAdminService, eventPublisher, serverService, modelMapper);
    }

    @Test
    void createUserFail_ExistingKeycloakId() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("mockEmail");
        mockUser.setDiscordId("20characterdiscordid");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setIsDeleted(false);

        //when a request is made to create user with the same keycloak id
        when(userRepository.findByKeycloakId("mockKeycloakId")).thenReturn(Optional.of(mockUser));

        //then
        assertAll("create Users fail assertion set: ExistingKeycloakId",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("mockKeycloakId", "none", "none",
                                "none", "none")),
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
        mockUser.setDiscordId("20characterdiscordid");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setIsDeleted(false);

        //when a create user request attempts to create a user with the same email
        when(userRepository.findByEmail("mockemail")).thenReturn(Optional.of(mockUser));

        //then
        assertAll("create Users fail assertion set: ExistingEmail",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("none", "none", "mockEmail",
                                "none", "none")),
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
        mockUser.setDiscordId("20characterdiscordid");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setIsDeleted(false);

        //when a create user request attempts to create a user with the same username
        when(userRepository.findByUsernameIgnoreCase("mockUsername")).thenReturn(Optional.of(mockUser));

        assertAll("create Users fail assertion set: ExistingUsername",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("none", "mockUsername", "none",
                                "none", "none")),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("User creation requested for existing username: "))),
                () -> verify(userRepository, times(1)).findByUsernameIgnoreCase("mockUsername"));
    }

    @Test
    void createUserFail_ExistingDiscordId() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("mockEmail");
        mockUser.setDiscordId("20characterdiscordid");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setIsDeleted(false);

        //when a create user request attempts to create a user with the same discordId
        when(userRepository.findByDiscordId("20characterdiscordid")).thenReturn(Optional.of(mockUser));

        assertAll("create Users fail assertion set: ExistingDiscordId",
                () -> assertThrows(UserConflictException.class, () ->
                        userService.createUser("none", "none", "none",
                                "20characterdiscordid", "none")),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("User creation requested for existing Discord ID: "))),
                () -> verify(userRepository, times(1)).findByDiscordId("20characterdiscordid"));
    }

    @Test
    void createUserSuccess_ExistingUser_IsDeleted() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("thisrawemail@gmail.com");
        mockUser.setDiscordId("20characterdiscordid");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setIsDeleted(true);

        // when a request is made to create user with the same userName and email, but the user isDeleted
        when(userRepository.findByUsernameIgnoreCase("mockUsername")).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("thisrawemail@gmail.com")).thenReturn(Optional.of(mockUser));

        PrivateUserResponseDto mockPrivateDto = new PrivateUserResponseDto();
        mockPrivateDto.setUsername("mockUsername");

        when(userConversionService.convertToPrivateDto(any(Users.class))).thenReturn(mockPrivateDto);

        PrivateUserResponseDto newMockUser = assertDoesNotThrow(
                () -> userService.createUser("newUuidMockKeycloakId", "mockUsername",
                        "   tHiSrAWemaIl@gmail.com    ", "20characterdiscordid", "mockDiscordUsername"),
                        "createUser should not throw when existing user is deleted"
        );

        //then
        assertAll("create Users fail assertion set: ExistingKeycloakId",
                () -> assertTrue(logCaptor.getErrorLogs().isEmpty()),
                () -> assertEquals(newMockUser.getUsername(), "mockUsername"),
                () -> verify(userRepository, times(1)).findByKeycloakId("newUuidMockKeycloakId"));
    }

    @Test
    void deleteUser_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        ServerRegion testServer = new ServerRegion();
        testServer.setServerId(1);
        testServer.setServerName("Banana");

        //given a mock user with all PII fields populated
        Users mockUser = new Users();
        mockUser.setUserId(123L);
        mockUser.setKeycloakId("mock-kc-id");
        mockUser.setUsername("someUsername");
        mockUser.setEmail("some@email.com");
        mockUser.setServer(testServer);
        mockUser.setOrg("SomeOrg");
        mockUser.setAbout("Some about text");
        mockUser.setDiscordId("12345678901234567890");
        mockUser.setDiscordUsername("mockDiscordUsername");
        mockUser.setExternalSysNotesEnabled(true);
        mockUser.setExternalGroupNotesEnabled(true);
        mockUser.setExternalSocialNotesEnabled(true);
        mockUser.setIsDeleted(false);

        when(userRepository.findByKeycloakId("mock-kc-id")).thenReturn(Optional.of(mockUser));

        //when
        userService.deleteUser("mock-kc-id");

        //then
        assertAll("deleteUser field overwrites",
                () -> assertEquals("deleted_123@deleted.com", mockUser.getEmail()),
                () -> assertEquals("deleted_123", mockUser.getUsername()),
                () -> assertEquals("deleted_123", mockUser.getKeycloakId()),
                () -> assertNull(mockUser.getServer()),
                () -> assertNull(mockUser.getOrg()),
                () -> assertNull(mockUser.getAbout()),
                () -> assertNull(mockUser.getDiscordId()),
                () -> assertNull(mockUser.getDiscordUsername()),
                () -> assertFalse(mockUser.getExternalSysNotesEnabled()),
                () -> assertFalse(mockUser.getExternalGroupNotesEnabled()),
                () -> assertFalse(mockUser.getExternalSocialNotesEnabled()),
                () -> assertTrue(mockUser.getIsDeleted()),
                () -> verify(groupListingRepository, times(1)).expireAllUserListingsOnDelete(123L),
                () -> verify(userRepository, times(1)).save(mockUser),
                () -> verify(eventPublisher).publishEvent(any(UserAccountDeleteEvent.class)),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Deleted user with id 123")))
        );
    }

    @Test
    void deleteUser_NotFound() {
        //given no user exists for the provided kcId
        when(userRepository.findByKeycloakId("unknown-kc-id")).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("unknown-kc-id"));
        verify(groupListingRepository, never()).expireAllUserListingsOnDelete(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_KeycloakFailure_StillCompletes() {
        LogCaptor logCaptor = LogCaptor.forClass(UserServiceImpl.class);

        //given a mock user
        Users mockUser = new Users();
        mockUser.setUserId(123L);
        mockUser.setKeycloakId("mock-kc-id");
        mockUser.setUsername("someUsername");
        mockUser.setEmail("some@email.com");
        mockUser.setIsDeleted(false);

        when(userRepository.findByKeycloakId("mock-kc-id")).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Keycloak unavailable")).when(kcAdminService).deleteKeycloakUser("mock-kc-id");

        //when/then — should not throw despite KC failure
        assertDoesNotThrow(() -> userService.deleteUser("mock-kc-id"));
        verify(userRepository, times(1)).save(any());
        verify(eventPublisher).publishEvent(any(UserAccountDeleteEvent.class));
        assertTrue(logCaptor.getWarnLogs().stream()
                .anyMatch(log -> log.contains("Keycloak unavailable")));
    }
}
