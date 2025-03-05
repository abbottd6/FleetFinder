package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupListingServiceImplTest {

    @Mock
    private GroupListingRepository groupListingRepository;

    @Mock
    private ModelMapper createGroupListingModelMapper;

    @Mock
    private MapperLookupService mapperLookupService;

    private static Validator validator;

    @InjectMocks
    private GroupListingServiceImpl groupListingService;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void getAllGroupListings_Found() {
        //given
        GroupListing mockListing1 = new GroupListing();
        GroupListing mockListing2 = new GroupListing();
        List<GroupListing> mockEntities = List.of(mockListing1, mockListing2);
        when(groupListingRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GroupListingResponseDto> result = groupListingService.getAllGroupListings();

        //then
        assertAll("get all group listings mock entities assertions set:",
                () -> assertNotNull(result, "get all listings should not be null"),
                () -> assertEquals(2, result.size(), "get all listings should have 2 elements here"),
                () -> verify(groupListingRepository, times(1)).findAll());
    }

    @Test
    void getAllGroupListings_NotFound() {
        //given
        when(groupListingRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GroupListingResponseDto> result = groupListingService.getAllGroupListings();

        //then
        assertAll("get all listings = empty assertions set: ",
                () -> assertNotNull(result, "get all listings should not be null"),
                () -> assertTrue(result.isEmpty(), "get all listings should be empty"),
                () -> verify(groupListingRepository, times(1)).findAll());
    }

    @Test
    void createGroupListingValidators_Fail() {
        //given
        CreateGroupListingDto createGroupListingDto = new CreateGroupListingDto();
            //fields are null

        Set<ConstraintViolation<CreateGroupListingDto>> dtoConstraintViolations = validator.validate(createGroupListingDto);

        //when fields are null

        //then create should fail
        assertAll("create listing failing validation assertions set:",
                () ->  assertFalse(dtoConstraintViolations.isEmpty()),
                //the following are true or false depending on whether null input should fail validation
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("userId")),
                        "blank userId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("serverId")),
                        "blank serverId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("environmentId")),
                        "blank environmentId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("experienceId")),
                        "blank experienceId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("listingTitle")),
                        "blank listingTitle should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("playStyleId")),
                        "blank playStyleId should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("legalityId")),
                        "blank legalityId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("groupStatusId")),
                        "blank groupStatusId should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventSchedule")),
                        "blank eventSchedule should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("categoryId")),
                        "blank categoryId should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("subcategoryId")),
                        "blank subcategoryId should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("pvpStatusId")),
                        "blank pvpStatusId should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("systemId")),
                        "blank systemId should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("planetId")),
                        "blank planetId should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("listingDescription")),
                        "blank listingDescription should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("desiredPartySize")),
                        "blank desiredPartySize should fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("currentPartySize")),
                        "blank currentPartySize should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("availableRoles")),
                        "blank availableRoles should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("commsOption")),
                        "blank commsOption should fail validation create listing"),
                () -> assertFalse(dtoConstraintViolations.stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("commsService")),
                        "blank commsService should NOT fail validation create listing"));
    }

    //testing HTTP response from successful create listing
    @Test
    void createGroupListing_Success_AllFields() {
        //given
        //creating valid dto
        CreateGroupListingDto validDto = new CreateGroupListingDto();
            validDto.setUserId(1L);
            validDto.setServerId(2);
            validDto.setEnvironmentId(2);
            validDto.setExperienceId(1);
            validDto.setListingTitle("This is a valid listing title");
            validDto.setPlayStyleId(5);
            validDto.setLegalityId(3);
            validDto.setGroupStatusId(2);
            validDto.setEventSchedule(Instant.now());
            validDto.setCategoryId(5);
            validDto.setSubcategoryId(16);
            validDto.setPvpStatusId(1);
            validDto.setSystemId(2);
            validDto.setPlanetId(6);
            validDto.setListingDescription("This is a valid listing description");
            validDto.setDesiredPartySize(5);
            validDto.setCurrentPartySize(2);
            validDto.setAvailableRoles("These are valid available roles");
            validDto.setCommsOption("Required");
            validDto.setCommsService("This is a valid comms service");

        //creating test user
        User testUser = new User();
            testUser.setUserId(1L);
            testUser.setUsername("TestUser");

        //returning new entities from lookup service for test only
        when(mapperLookupService.findUserById(validDto.getUserId())).thenReturn(testUser);

        GroupListing mappedEntity = new GroupListing();
        //setting groupId to imitate autogenerate from the database
            mappedEntity.setGroupId(1L);
            mappedEntity.setUser(mapperLookupService.findUserById(validDto.getUserId()));
            mappedEntity.setListingTitle(validDto.getListingTitle());

        when(createGroupListingModelMapper.map(validDto, GroupListing.class)).thenReturn(mappedEntity);
        when(groupListingRepository.save(any(GroupListing.class))).thenReturn(mappedEntity);

        //when
        ResponseEntity<?> response = groupListingService.createGroupListing(validDto);

        //then
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertAll("successful create listing assertions set: ",
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("This is a valid listing title",  responseBody.get("listingTitle")),
                () -> verify(createGroupListingModelMapper, times(1)).map(validDto, GroupListing.class),
                () -> verify(groupListingRepository, times(1)).save(any(GroupListing.class)),
                () -> verify(mapperLookupService, times(1)).findUserById(validDto.getUserId()),
                () -> verifyNoMoreInteractions(createGroupListingModelMapper, groupListingRepository));
    }


    //testing http response from unsuccessful create listing
    @Test
    void testCreateGroupListing_Fail() {
        //given
        CreateGroupListingDto invalidDto = new CreateGroupListingDto();

        when(createGroupListingModelMapper.map(invalidDto, GroupListing.class))
                .thenThrow(new RuntimeException("simulated mapping or database error"));

        //when
        ResponseEntity<?> response = groupListingService.createGroupListing(invalidDto);

        //then
        assertAll("create listing failed assertions set:",
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals("An error occurred while creating the listing.", response.getBody()),
                () -> verify(createGroupListingModelMapper, times(1)).map(invalidDto, GroupListing.class),
                () -> verifyNoMoreInteractions(createGroupListingModelMapper, groupListingRepository));
    }

    @Test
    @Disabled
    void testUpdateGroupListing() {
    }

    @Test
    @Disabled
    void testDeleteGroupListing() {
    }

    @Test
    @Disabled
    void testGetGroupListingById_Success() {

    }

    @Test
    @Disabled
    void testConvertListingToDto() {
    }

    @Test
    @Disabled
    void testConvertToEntity() {
    }
}