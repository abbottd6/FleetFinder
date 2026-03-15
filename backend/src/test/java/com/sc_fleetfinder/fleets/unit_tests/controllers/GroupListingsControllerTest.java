package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.GroupListingsController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GroupListingsController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class GroupListingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupListingService groupListingService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private List<GroupListingResponseDto> mockGroupListings;

    @BeforeEach
    void setUp() {
        GroupListingResponseDto mockListing1 = new GroupListingResponseDto();
        mockListing1.setGroupId(1L);
        mockListing1.setUserName("TestUser");
        mockListing1.setServer("USA");
        mockListing1.setEnvironment("Live");
        mockListing1.setExperience("Persistent Universe");
        mockListing1.setListingTitle("Test listing1 title");
        mockListing1.setPlayStyle("Casual");
        mockListing1.setLegality("Lawful");
        mockListing1.setGroupStatus("Future/Scheduled");
        mockListing1.setEventSchedule(Instant.now().truncatedTo(ChronoUnit.MINUTES));
        mockListing1.setCategory("Medical");
        mockListing1.setSubcategory("For Hire");
        mockListing1.setPvpStatus("PvX");
        mockListing1.setSystem("Stanton");
        mockListing1.setPlanetMoonSystem("Stanton I");
        mockListing1.setListingDescription("This is a description");
        mockListing1.setDesiredPartySize(3);
        mockListing1.setCurrentPartySize(1);
        mockListing1.setAvailableRoles("Here are some available roles");
        mockListing1.setCommsOption("Required");
        mockListing1.setCommsService("This is a comms service");
        mockListing1.setLanguageCode(LanguageOptions.English);
        mockListing1.setCreationTimestamp(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));
        mockListing1.setLastUpdated(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));



        GroupListingResponseDto mockListing2 = new GroupListingResponseDto();
        mockListing2.setGroupId(2L);
        mockListing2.setUserName("DifferentTestUser");
        mockListing2.setServer("AUS");
        mockListing2.setEnvironment("PTU");
        mockListing2.setExperience("Persistent Universe");
        mockListing2.setListingTitle("Different listing title");
        mockListing2.setPlayStyle("Competitive");
        mockListing2.setLegality("Unlawful");
        mockListing2.setGroupStatus("Current/Live");
        mockListing2.setEventSchedule(null);
        mockListing2.setCategory("Ship Combat");
        mockListing2.setSubcategory("Dueling");
        mockListing2.setPvpStatus("PvP");
        mockListing2.setSystem("Pyro");
        mockListing2.setPlanetMoonSystem("Pyro I");
        mockListing2.setListingDescription("This is a better description");
        mockListing2.setDesiredPartySize(2);
        mockListing2.setCurrentPartySize(1);
        mockListing2.setAvailableRoles("Here are some available roles");
        mockListing2.setCommsOption("Optional");
        mockListing2.setCommsService("");
        mockListing2.setLanguageCode(LanguageOptions.English);
        mockListing2.setCreationTimestamp(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));
        mockListing2.setLastUpdated(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));

        mockGroupListings = Arrays.asList(mockListing1, mockListing2);
    }

    @Test
    void getGroupAllGroupListings_NotFound() throws Exception {
        when(groupListingService.getAllGroupListings())
                .thenThrow(new ResourceNotFoundException("There are no current group listings"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/group-listings"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGroupListingById_Found() throws Exception{
        when(groupListingService.getGroupListingById(1L)).thenReturn(mockGroupListings.getFirst());

        //results are matched to mockListing1 in @BeforeEach
        mockMvc.perform(MockMvcRequestBuilders.get("/api/group-listings/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.groupId")
                        .value(1L))
                .andExpect(jsonPath("$.userName")
                        .value("TestUser"))
                .andExpect(jsonPath("$.server")
                        .value("USA"))
                .andExpect(jsonPath("$.environment")
                        .value("Live"))
                .andExpect(jsonPath("$.experience")
                        .value("Persistent Universe"))
                .andExpect(jsonPath("$.listingTitle")
                        .value("Test listing1 title"))
                .andExpect(jsonPath("$.playStyle")
                        .value("Casual"))
                .andExpect(jsonPath("$.legality")
                        .value("Lawful"))
                .andExpect(jsonPath("$.groupStatus")
                        .value("Future/Scheduled"))
                .andExpect(jsonPath("$.eventSchedule").exists())
                .andExpect(jsonPath("$.eventSchedule")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$.category")
                        .value("Medical"))
                .andExpect(jsonPath("$.subcategory")
                        .value("For Hire"))
                .andExpect(jsonPath("$.pvpStatus")
                        .value("PvX"))
                .andExpect(jsonPath("$.system")
                        .value("Stanton"))
                .andExpect(jsonPath("$.planetMoonSystem")
                        .value("Stanton I"))
                .andExpect(jsonPath("$.listingDescription")
                        .value("This is a description"))
                .andExpect(jsonPath("$.desiredPartySize")
                        .value(3))
                .andExpect(jsonPath("$.currentPartySize")
                        .value(1))
                .andExpect(jsonPath("$.availableRoles")
                        .value("Here are some available roles"))
                .andExpect(jsonPath("$.commsOption")
                        .value("Required"))
                .andExpect(jsonPath("$.commsService")
                        .value("This is a comms service"))
                .andExpect(jsonPath("$.languageCode")
                        .value("English"))
                .andExpect(jsonPath("$.creationTimestamp")
                        .exists())
                .andExpect(jsonPath("$.creationTimestamp")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$.lastUpdated").exists())
                .andExpect(jsonPath("$.lastUpdated")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));
    }

    @Test
    void testGetGroupListingById_NotFound() throws Exception {
        when(groupListingService.getGroupListingById(33L)).thenThrow(new ResourceNotFoundException(33L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/group-listings/33"))
                .andExpect(status().isNotFound());
    }

    //checks response for group listing creation when all createGroupListingDto fields contain valid values
    @Test
    void testCreateGroupListing_SuccessAllFields() throws Exception {
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
            mockDto.setUserId(1L);
            mockDto.setServerId(1);
            mockDto.setEnvironmentId(2);
            mockDto.setExperienceId(1);
            mockDto.setListingTitle("Create listing mock dto title");
            mockDto.setPlayStyleId(1);
            mockDto.setLegalityId(2);
            mockDto.setGroupStatusId(2);
            mockDto.setEventDate("2025-03-15");
            mockDto.setEventTime("18:30:00Z");
            mockDto.setEventTimeZone("Pacific Standard Time");
            mockDto.setCategoryId(1);
            mockDto.setSubcategoryId(1);
            mockDto.setPvpStatusId(1);
            mockDto.setSystemId(1);
            mockDto.setPlanetId(1);
            mockDto.setListingDescription("Create listing mock dto description");
            mockDto.setDesiredPartySize(3);
            mockDto.setCurrentPartySize(1);
            mockDto.setAvailableRoles("mock roles");
            mockDto.setCommsOption("Optional");
            mockDto.setCommsService("Discord");
            mockDto.setLanguageCode(LanguageOptions.English);

        Map<String, String> response = new HashMap<>();
        response.put("listingTitle", mockDto.getListingTitle());

        Users mockUser = new Users();
        mockUser.setUserId(12L);
        mockUser.setUsername("mock user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("mockuser@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class), any(Users.class));


        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listingTitle").value("Create listing mock dto title"));
    }

    //Checks whether nullable fields as null will pass validation
    @Test
    void testCreateGroupListing_SuccessOnlyRequiredFields() throws Exception {
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
        mockDto.setUserId(1L);
        mockDto.setServerId(1);
        mockDto.setEnvironmentId(2);
        mockDto.setExperienceId(1);
        mockDto.setListingTitle("Create listing mock dto title");
        mockDto.setPlayStyleId(null);
        mockDto.setLegalityId(2);
        mockDto.setGroupStatusId(2);
        mockDto.setEventDate(null);
        mockDto.setEventTime(null);
        mockDto.setEventTimeZone(null);
        mockDto.setCategoryId(1);
        mockDto.setSubcategoryId(null);
        mockDto.setPvpStatusId(1);
        mockDto.setSystemId(1);
        mockDto.setPlanetId(null);
        mockDto.setListingDescription("Create listing mock dto description");
        mockDto.setDesiredPartySize(3);
        mockDto.setCurrentPartySize(1);
        mockDto.setAvailableRoles(null);
        mockDto.setCommsOption("Optional");
        mockDto.setCommsService(null);
        mockDto.setLanguageCode(LanguageOptions.English);

        Map<String, String> response = new HashMap<>();
        response.put("listingTitle", mockDto.getListingTitle());

        Users mockUser = new Users();
        mockUser.setUserId(12L);
        mockUser.setUsername("mock user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("mockuser@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class), any(Users.class));


        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listingTitle").value("Create listing mock dto title"));
    }

    //createGroupListing failure within the method logic
    @Test
    void testCreateGroupListing_Fail() throws Exception {
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
//        mockDto.setUserId(1L);
        mockDto.setServerId(1);
        mockDto.setEnvironmentId(2);
        mockDto.setExperienceId(1);
        mockDto.setListingTitle("Create listing mock dto title");
        mockDto.setPlayStyleId(1);
        mockDto.setLegalityId(2);
        mockDto.setGroupStatusId(2);
        mockDto.setEventDate("2025-03-15");
        mockDto.setEventTime("18:30:00Z");
        mockDto.setEventTimeZone("Pacific Standard Time");
        mockDto.setCategoryId(1);
        mockDto.setSubcategoryId(1);
        mockDto.setPvpStatusId(1);
        mockDto.setSystemId(1);
        mockDto.setPlanetId(1);
        mockDto.setListingDescription("Create listing mock dto description");
        mockDto.setDesiredPartySize(3);
        mockDto.setCurrentPartySize(1);
        mockDto.setAvailableRoles("mock roles");
        mockDto.setCommsOption("Optional");
        mockDto.setCommsService("Discord");
        mockDto.setLanguageCode(LanguageOptions.English);

        Users mockUser = new Users();
        mockUser.setUserId(12L);
        mockUser.setUsername("mock user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("mockuser@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating your listing");
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class), any(Users.class));

        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while creating your listing"));
    }

    //verifies validation error messages are present from createGroupListingDto
    @Test
    void testCreateGroupListing_ValidationFail() throws Exception {
        CreateGroupListingDto invalidDto = new CreateGroupListingDto();

        //empty dto: fails all validations

        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverId")
                        .value("Create group listing DTO field 'serverId' cannot be null"))
                .andExpect(jsonPath("$.environmentId")
                        .value("Create group listing DTO field 'environmentId' cannot be null"))
                .andExpect(jsonPath("$.experienceId")
                        .value("Create group listing DTO field 'experienceId' cannot be null"))
                .andExpect(jsonPath("$.listingTitle")
                        .value("Create group listing DTO field 'listingTitle' cannot be blank"))
                .andExpect(jsonPath("$.legalityId")
                        .value("Create group listing DTO field 'legalityId' cannot be null"))
                .andExpect(jsonPath("$.groupStatusId")
                        .value("Create group listing DTO field 'groupStatusId' cannot be null"))
                .andExpect(jsonPath("$.categoryId")
                        .value("Create group listing DTO field 'categoryId' cannot be null"))
                .andExpect(jsonPath("$.pvpStatusId")
                        .value("Create group listing DTO field 'pvpStatusId' cannot be null"))
                .andExpect(jsonPath("$.systemId")
                        .value("Create group listing DTO field 'systemId' cannot be null"))
                .andExpect(jsonPath("$.listingDescription")
                        .value("Create group listing DTO field 'listingDescription' cannot be blank"))
                .andExpect(jsonPath("$.desiredPartySize")
                        .value("Create group listing DTO field 'desiredPartySize' cannot be null"))
                .andExpect(jsonPath("$.currentPartySize")
                        .value("Create group listing DTO field 'currentPartySize' cannot be null"))
                .andExpect(jsonPath("$.commsOption")
                        .value("Create listing DTO field 'commsOption' cannot be null"))
                .andExpect(jsonPath("$.languageCode")
                        .value("CreateGroupListingDto field 'languageCode' cannot be null"));
    }

    @Test
    void testCreateGroupListing_BadRequest_InvalidLanguageCode() throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("serverId", 1);
        body.put("environmentId", 1);
        body.put("experienceId", 1);
        body.put("listingTitle", "Some valid title");
        body.put("legalityId", 1);
        body.put("groupStatusId", 1);
        body.put("categoryId", 1);
        body.put("pvpStatusId", 1);
        body.put("systemId", 1);
        body.put("listingDescription", "Some valid description");
        body.put("desiredPartySize", 5);
        body.put("currentPartySize", 2);
        body.put("commsOption", "Optional");
        body.put("languageCode", "Klingon"); // not a valid LanguageOptions constant

        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateGroupListing_Success() throws Exception {
        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(1L);
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Updated listing title");
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setCategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setListingDescription("Updated listing description here.");
        dto.setDesiredPartySize(3);
        dto.setCurrentPartySize(1);
        dto.setCommsOption("Optional");
        dto.setLanguageCode(LanguageOptions.English);

        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("test@test.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        Map<String, String> response = new HashMap<>();
        response.put("listingTitle", "Updated listing title");
        doAnswer(inv -> ResponseEntity.status(HttpStatus.OK).body(response))
                .when(groupListingService).updateGroupListing(any(UpdateGroupListingDto.class), any(Users.class));

        mockMvc.perform(put("/api/group-listings/update_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listingTitle").value("Updated listing title"));
    }

    @Test
    void updateGroupListing_NotFound() throws Exception {
        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(999L);
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Updated listing title");
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setCategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setListingDescription("Updated listing description here.");
        dto.setDesiredPartySize(3);
        dto.setCurrentPartySize(1);
        dto.setCommsOption("Optional");
        dto.setLanguageCode(LanguageOptions.English);

        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("test@test.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group listing not found."))
                .when(groupListingService).updateGroupListing(any(UpdateGroupListingDto.class), any(Users.class));

        mockMvc.perform(put("/api/group-listings/update_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGroupListing_Unauthorized() throws Exception {
        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(1L);
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Updated listing title");
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setCategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setListingDescription("Updated listing description here.");
        dto.setDesiredPartySize(3);
        dto.setCurrentPartySize(1);
        dto.setCommsOption("Optional");
        dto.setLanguageCode(LanguageOptions.English);

        Users mockUser = new Users();
        mockUser.setUserId(2L);
        mockUser.setUsername("DifferentUser");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("different@test.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(inv -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized to update this listing."))
                .when(groupListingService).updateGroupListing(any(UpdateGroupListingDto.class), any(Users.class));

        mockMvc.perform(put("/api/group-listings/update_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGroupListing_ValidationFail() throws Exception {
        UpdateGroupListingDto invalidDto = new UpdateGroupListingDto();
        // empty dto — fails all @NotNull/@NotBlank validations

        mockMvc.perform(put("/api/group-listings/update_listing")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteGroupListing_Success() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("mock user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("mockuser@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        Map<String, String> response = new HashMap<>();
        response.put("listingId", "1");
        doAnswer(inv -> ResponseEntity.status(HttpStatus.OK).body(response))
                .when(groupListingService).deleteGroupListing(anyLong(), any(Users.class));

        mockMvc.perform(delete("/api/group-listings/delete_listing/1")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listingId").value("1"));
    }

    @Test
    void deleteGroupListing_NotFound() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("mock user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("mockuser@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(inv -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group listing not found."))
                .when(groupListingService).deleteGroupListing(anyLong(), any(Users.class));

        mockMvc.perform(delete("/api/group-listings/delete_listing/500")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGroupListing_Unauthorized() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(2L);
        mockUser.setUsername("different user");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("different@gmail.com");
        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        doAnswer(inv -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized to delete this listing."))
                .when(groupListingService).deleteGroupListing(anyLong(), any(Users.class));

        mockMvc.perform(delete("/api/group-listings/delete_listing/1")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isUnauthorized());
    }
}