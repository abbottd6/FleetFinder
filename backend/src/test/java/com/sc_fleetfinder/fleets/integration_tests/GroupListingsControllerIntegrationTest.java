package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.Disabled;
import tools.jackson.databind.ObjectMapper;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class GroupListingsControllerIntegrationTest extends AbstractIntegrationTestDB{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MapperLookupService mapperLookupService;

    @Test
    @Disabled
    void testGetAllGroupListings_Success() throws Exception {
        mockMvc.perform(get("/api/group-listings")
                .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("_embedded.groupListingResponseDtoes[0].groupId").value("1"));
    }

    @Test
    void testGetGroupListingByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/group-listings/1")
                .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId").value("1"))
                .andExpect(jsonPath(
                        "$.userName").value("TestUser"))
                .andExpect(jsonPath(
                        "$.server").value("USA"))
                .andExpect(jsonPath(
                        "$.environment").value("LIVE"))
                .andExpect(jsonPath(
                        "$.experience")
                        .value("Persistent Universe"))
                .andExpect(jsonPath(
                        "$.listingTitle")
                        .value("Integration testing title"))
                .andExpect(jsonPath(
                        "$.playStyle")
                        .value("Stream - Watchable"))
                .andExpect(jsonPath(
                        "$.legality").value("Lawful"))
                .andExpect(jsonPath(
                        "$.groupStatus")
                        .value("Future/Scheduled"))
                .andExpect(jsonPath(
                        "$.eventSchedule")
                        .value("2025-03-15T18:30:00Z"))
                .andExpect(jsonPath(
                        "$.category").value("Mining"))
                .andExpect(jsonPath(
                        "$.subcategory")
                        .value("Prospecting"))
                .andExpect(jsonPath(
                        "$.pvpStatus").value("PvX"))
                .andExpect(jsonPath(
                        "$.system").value("Stanton"))
                .andExpect(jsonPath(
                        "$.planetMoonSystem")
                        .value("MicroTech: Stanton IV"))
                .andExpect(jsonPath(
                        "$.listingDescription")
                        .value("Preloading test data for integration tests."))
                .andExpect(jsonPath(
                        "$.desiredPartySize").value(2))
                .andExpect(jsonPath(
                        "$.currentPartySize").value(1))
                .andExpect(jsonPath(
                        "$.availableRoles").value("Any"))
                .andExpect(jsonPath(
                        "$.commsOption").value("Optional"))
                .andExpect(jsonPath(
                        "$.commsService").value("Discord"))
                .andExpect(jsonPath("$.creationTimestamp").exists())
                .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void testGetGroupListingByIdFailure() throws Exception {
        mockMvc.perform(get("/api/group-listings/500")
                .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateGroupListingSuccess_AllFields() throws Exception {
        //creating a 'createGroupListingDto' to mock a user creating a listing
        CreateGroupListingDto testDto = new CreateGroupListingDto();
            testDto.setServerId(1);
            testDto.setEnvironmentId(1);
            testDto.setExperienceId(1);
            testDto.setListingTitle("Integration test group listing");
            testDto.setPlayStyleId(1); // Optional, can be null
            testDto.setLegalityId(1);
            testDto.setGroupStatusId(2);
            testDto.setEventDate("2025-03-15"); // Optional
            testDto.setEventTime("18:30:00");
            testDto.setEventTimeZone("America/Los_Angeles");
            testDto.setCategoryId(1);
            testDto.setSubcategoryId(1); // Optional
            testDto.setPvpStatusId(1);
            testDto.setSystemId(1);
            testDto.setPlanetId(1); // Optional
            testDto.setListingDescription("This is a test description used for integration testing.");
            testDto.setDesiredPartySize(5);
            testDto.setCurrentPartySize(2);
            testDto.setAvailableRoles("Medic, Sniper"); // Optional
            testDto.setCommsOption("Optional");
            testDto.setCommsService("Discord");

//        Users mockUser = new Users();
//        mockUser.setUsername("mock user");
//        mockUser.setKeycloakId("someKeycloakId");
//        mockUser.setEmail("mockuser@gmail.com");
//        userRepository.save(mockUser);
//        userRepository.flush();

        //posting the listing to call createGroupListing
        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andReturn();

        //querying for largest group listing id to get the id of the listing that was just created
        Long testId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        //performing a get request for this id to check the response dto matches what was input in the createListingDto
        mockMvc.perform(get("/api/group-listings/" + testId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.server").value("USA"))
                .andExpect(jsonPath("$.environment").value("LIVE"))
                .andExpect(jsonPath("$.experience").value("Persistent Universe"))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andExpect(jsonPath("$.playStyle").value("Casual"))
                .andExpect(jsonPath("$.legality").value("Lawful"))
                .andExpect(jsonPath("$.groupStatus").value("Future/Scheduled"))
                .andExpect(jsonPath("$.eventSchedule").value("2025-03-16T01:30:00Z"))
                .andExpect(jsonPath("$.category").value("Commerce/Trade"))
                .andExpect(jsonPath("$.subcategory").value("Bounty Hunting PVP"))
                .andExpect(jsonPath("$.pvpStatus").value("PvP"))
                .andExpect(jsonPath("$.system").value("Stanton"))
                .andExpect(jsonPath("$.planetMoonSystem").value("Hurston: Stanton I"))
                .andExpect(jsonPath("$.desiredPartySize").value(5))
                .andExpect(jsonPath("$.currentPartySize").value(2))
                .andExpect(jsonPath("$.availableRoles").value("Medic, Sniper"))
                .andExpect(jsonPath("$.commsOption").value("Optional"))
                .andExpect(jsonPath("$.commsService").value("Discord"));
    }

    @Test
    void testCreateGroupListingSuccess_minimumRequiredFields() throws Exception {
//        Users mockUser = new Users();
//        mockUser.setUsername("mock user");
//        mockUser.setKeycloakId("someKeycloakId");
//        mockUser.setEmail("mockuser@gmail.com");
//        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        //creating test listing that does NOT include optional fields
        CreateGroupListingDto testDto = new CreateGroupListingDto();
        testDto.setServerId(1);
        testDto.setEnvironmentId(1);
        testDto.setExperienceId(1);
        testDto.setListingTitle("Integration test group listing");
        testDto.setLegalityId(1);
        testDto.setGroupStatusId(1);
        testDto.setCategoryId(1);
        testDto.setPvpStatusId(1);
        testDto.setSystemId(1);
        testDto.setListingDescription("This is a test description used for integration testing.");
        testDto.setDesiredPartySize(5);
        testDto.setCurrentPartySize(2);
        testDto.setCommsOption("Optional");

        //posting the listing to call createGroupListing
        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andReturn();

        //querying for largest group listing id to get the id of the listing that was just created
        Long testId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        //performing a get request for this id to check the response dto matches what was input in the createListingDto
        mockMvc.perform(get("/api/group-listings/" + testId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.server").value("USA"))
                .andExpect(jsonPath("$.environment").value("LIVE"))
                .andExpect(jsonPath("$.experience").value("Persistent Universe"))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andExpect(jsonPath("$.playStyle").value(""))
                .andExpect(jsonPath("$.legality").value("Lawful"))
                .andExpect(jsonPath("$.groupStatus").value("Current/Live"))
                .andExpect(jsonPath("$.eventSchedule", nullValue()))
                .andExpect(jsonPath("$.category").value("Commerce/Trade"))
                .andExpect(jsonPath("$.subcategory").value(""))
                .andExpect(jsonPath("$.pvpStatus").value("PvP"))
                .andExpect(jsonPath("$.system").value("Stanton"))
                .andExpect(jsonPath("$.planetMoonSystem").value(""))
                .andExpect(jsonPath("$.desiredPartySize").value(5))
                .andExpect(jsonPath("$.currentPartySize").value(2))
                .andExpect(jsonPath("$.availableRoles").value(""))
                .andExpect(jsonPath("$.commsOption").value("Optional"))
                .andExpect(jsonPath("$.commsService").value(""));
    }

    @Test
    void testCreateGroupListing_Fail_Invalid() throws Exception{
        CreateGroupListingDto testDto = new CreateGroupListingDto();
        //This ID should fail
        testDto.setUserId(350L);
        //or this server id should fail
        testDto.setServerId(5000);
        testDto.setEnvironmentId(1);
        testDto.setExperienceId(1);
        testDto.setListingTitle("Integration test group listing");
        testDto.setLegalityId(1);
        testDto.setGroupStatusId(1);
        testDto.setCategoryId(1);
        testDto.setPvpStatusId(1);
        testDto.setSystemId(1);
        testDto.setListingDescription("This is a test description used for integration testing.");
        testDto.setDesiredPartySize(5);
        testDto.setCurrentPartySize(2);
        testDto.setCommsOption("Optional");

//        Users mockUser = new Users();
//        mockUser.setUserId(12L);
//        mockUser.setUsername("mock user");
//        mockUser.setKeycloakId("someKeycloakId");
//        mockUser.setEmail("mockuser@gmail.com");
//        when(userRepository.findByKeycloakId("someKeycloakId")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while creating your listing."));
    }

    @Test
    void testCreateGroupListing_BadRequest_MissingRequiredFields() throws Exception {
        CreateGroupListingDto testDto = new CreateGroupListingDto();

        mockMvc.perform(post("/api/group-listings/create_listing")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "someKeycloakId")))
            .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isBadRequest());
    }
}
