package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.controllers.GroupListingsController;
import com.sc_fleetfinder.fleets.services.GroupListingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = GroupListingsController.class)
class GroupListingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupListingService groupListingService;

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
        mockListing2.setCreationTimestamp(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));
        mockListing2.setLastUpdated(Instant.parse(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));

        mockGroupListings = Arrays.asList(mockListing1, mockListing2);
    }

    @Test
    void testGetAllGroupListings_FoundList() throws Exception {
        when(groupListingService.getAllGroupListings()).thenReturn(mockGroupListings);

        //Results for [0] are matched to mockListing1 and [1] is matched to mockListing2 in @BeforeEach
        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupListings")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes.size()").value(2))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].groupId")
                        .value(1L))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].userName")
                        .value("TestUser"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].server")
                        .value("USA"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].environment")
                        .value("Live"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].experience")
                        .value("Persistent Universe"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].listingTitle")
                        .value("Test listing1 title"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].playStyle")
                        .value("Casual"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].legality")
                        .value("Lawful"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].groupStatus")
                        .value("Future/Scheduled"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].eventSchedule")
                        .exists())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].eventSchedule")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].category")
                        .value("Medical"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].subcategory")
                        .value("For Hire"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].pvpStatus")
                        .value("PvX"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].system")
                        .value("Stanton"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].planetMoonSystem")
                        .value("Stanton I"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].listingDescription")
                        .value("This is a description"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].desiredPartySize")
                        .value(3))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].currentPartySize")
                        .value(1))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].availableRoles")
                        .value("Here are some available roles"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].commsOption")
                        .value("Required"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].commsService")
                        .value("This is a comms service"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].creationTimestamp")
                        .exists())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].creationTimestamp")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].lastUpdated").exists())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[0].lastUpdated")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                //
                //
                //second listing matched to mockListing2 from @BeforeEach
                //
                //
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].groupId")
                        .value(2L))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].userName")
                        .value("DifferentTestUser"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].server")
                        .value("AUS"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].environment")
                        .value("PTU"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].experience")
                        .value("Persistent Universe"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].listingTitle")
                        .value("Different listing title"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].playStyle")
                        .value("Competitive"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].legality")
                        .value("Unlawful"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].groupStatus")
                        .value("Current/Live"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].eventSchedule").isEmpty())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].category")
                        .value("Ship Combat"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].subcategory")
                        .value("Dueling"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].pvpStatus")
                        .value("PvP"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].system")
                        .value("Pyro"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].planetMoonSystem")
                        .value("Pyro I"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].listingDescription")
                        .value("This is a better description"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].desiredPartySize")
                        .value(2))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].currentPartySize")
                        .value(1))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].availableRoles")
                        .value("Here are some available roles"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].commsOption")
                        .value("Optional"))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].commsService")
                        .value(""))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].creationTimestamp").exists())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].creationTimestamp")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].lastUpdated").exists())
                .andExpect(jsonPath("$._embedded.groupListingResponseDtoes[1].lastUpdated")
                        .value(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()));
    }

    @Test
    void getGroupAllGroupListings_NotFound() throws Exception {
        when(groupListingService.getAllGroupListings())
                .thenThrow(new ResourceNotFoundException("There are no current group listings"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupListings"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGroupListingById_Found() throws Exception{
        when(groupListingService.getGroupListingById(1L)).thenReturn(mockGroupListings.getFirst());

        //results are matched to mockListing1 in @BeforeEach
        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupListings/1")
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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupListings/33"))
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
            mockDto.setEventSchedule(Instant.now().truncatedTo(ChronoUnit.MINUTES));
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

        Map<String, String> response = new HashMap<>();
        response.put("listingTitle", mockDto.getListingTitle());

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class));


        mockMvc.perform(post("/api/groupListings/create_listing")
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
        mockDto.setEventSchedule(null);
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

        Map<String, String> response = new HashMap<>();
        response.put("listingTitle", mockDto.getListingTitle());

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class));


        mockMvc.perform(post("/api/groupListings/create_listing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listingTitle").value("Create listing mock dto title"));
    }

    //createGroupListing failure within the method logic
    @Test
    void testCreateGroupListing_Fail() throws Exception {
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
        mockDto.setUserId(1L);
        mockDto.setServerId(1);
        mockDto.setEnvironmentId(2);
        mockDto.setExperienceId(1);
        mockDto.setListingTitle("Create listing mock dto title");
        mockDto.setPlayStyleId(1);
        mockDto.setLegalityId(2);
        mockDto.setGroupStatusId(2);
        mockDto.setEventSchedule(Instant.now().truncatedTo(ChronoUnit.MINUTES));
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

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating your listing");
        }).when(groupListingService).createGroupListing(any(CreateGroupListingDto.class));

        mockMvc.perform(post("/api/groupListings/create_listing")
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

        mockMvc.perform(post("/api/groupListings/create_listing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId")
                        .value("Create group listing DTO field 'userId' cannot be null"))
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
                        .value("Create listing DTO field 'commsOption' cannot be null"));
    }


    @Test
    @Disabled
    void updateGroupListing() {
    }

    @Test
    @Disabled
    void deleteGroupListing() {
    }
}