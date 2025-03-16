package com.sc_fleetfinder.fleets.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
public class GroupListingsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllGroupListings_Success() throws Exception {
        mockMvc.perform(get("/api/groupListings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("_embedded.groupListingResponseDtoes[0].groupId").value("1"));
    }

    @Test
    void testGetGroupListingByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/groupListings/1"))
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
                        .value("Skill Development"))
                .andExpect(jsonPath(
                        "$.legality").value("Lawful"))
                .andExpect(jsonPath(
                        "$.groupStatus")
                        .value("Future/Scheduled"))
                .andExpect(jsonPath(
                        "$.eventSchedule")
                        .value("2025-03-15T18:30:00Z"))
                .andExpect(jsonPath(
                        "$.category").value("Mission"))
                .andExpect(jsonPath(
                        "$.subcategory")
                        .value("Maintenance"))
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
        mockMvc.perform(get("/api/groupListings/500"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateGroupListingSuccess_AllFields() throws Exception {

        //creating a 'createGroupListingDto' to mock a user creating a listing
        CreateGroupListingDto testDto = new CreateGroupListingDto();
            testDto.setUserId(1L);
            testDto.setServerId(1);
            testDto.setEnvironmentId(1);
            testDto.setExperienceId(1);
            testDto.setListingTitle("Integration test group listing");
            testDto.setPlayStyleId(1); // Optional, can be null
            testDto.setLegalityId(1);
            testDto.setGroupStatusId(2);
            testDto.setEventSchedule(Instant.parse("2025-03-15T18:30:00Z")); // Optional
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

        //posting the listing to call createGroupListing
        mockMvc.perform(post("/api/groupListings/create_listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andReturn();

        //querying for largest group listing id to get the id of the listing that was just created
        Long testId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        //performing a get request for this id to check the response dto matches what was input in the createListingDto
        mockMvc.perform(get("/api/groupListings/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.server").value("USA"))
                .andExpect(jsonPath("$.environment").value("LIVE"))
                .andExpect(jsonPath("$.experience").value("Persistent Universe"))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andExpect(jsonPath("$.playStyle").value("Casual"))
                .andExpect(jsonPath("$.legality").value("Lawful"))
                .andExpect(jsonPath("$.groupStatus").value("Future/Scheduled"))
                .andExpect(jsonPath("$.eventSchedule").value("2025-03-15T18:30:00Z"))
                .andExpect(jsonPath("$.category").value("Ship Combat"))
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
        //creating test listing that does NOT include optional fields
        CreateGroupListingDto testDto = new CreateGroupListingDto();
        testDto.setUserId(1L);
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
        mockMvc.perform(post("/api/groupListings/create_listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listingTitle").value("Integration test group listing"))
                .andReturn();

        //querying for largest group listing id to get the id of the listing that was just created
        Long testId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        //performing a get request for this id to check the response dto matches what was input in the createListingDto
        mockMvc.perform(get("/api/groupListings/" + testId))
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
                .andExpect(jsonPath("$.category").value("Ship Combat"))
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

        mockMvc.perform(post("/api/groupListings/create_listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while creating your listing."));
    }

    @Test
    void testCreateGroupListing_BadRequest_MissingRequiredFields() throws Exception {
        CreateGroupListingDto testDto = new CreateGroupListingDto();

        mockMvc.perform(post("/api/groupListings/create_listing")
            .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isBadRequest());
    }
}
