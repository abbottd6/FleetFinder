package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SearchListingsDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingServiceImpl;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

    @Mock
    private GroupListingConversionServiceImpl groupListingConversionService;

    @Mock
    private ArchiveService archiveService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationOutboxRepository notificationOutboxRepository;

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
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
        //given
        GroupListing mockListing1 = new GroupListing();
        GroupListing mockListing2 = new GroupListing();
        List<GroupListing> mockEntities = List.of(mockListing1, mockListing2);
        when(groupListingRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GroupListingResponseDto> result = groupListingService.getAllGroupListings();

        //then
        assertAll("get all group listings mock entities assertions set:",
                () -> assertEquals(0, logCaptor.getInfoLogs().size(), "getAllGroupListings should find " +
                        "listings and not produce any info logs."),
                () -> assertNotNull(result, "get all listings should not be null"),
                () -> assertEquals(2, result.size(), "get all listings should have 2 elements here"),
                () -> verify(groupListingRepository, times(1)).findAll());
    }

    @Test
    void getAllGroupListings_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
        //given
        when(groupListingRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GroupListingResponseDto> result = groupListingService.getAllGroupListings();

        //then
        assertAll("get all listings = empty assertions set: ",
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("No group listings found."))),
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
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals("listingCommsService")),
                        "blank listingCommsService should NOT fail validation create listing"),
                () -> assertTrue(dtoConstraintViolations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("languageCode")),
                        "blank languageCode should fail validation"));
    }

    //testing HTTP response from successful create listing
    @Test
    void createGroupListing_Success_AllFields() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
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
            validDto.setEventDate("2025-03-15");
            validDto.setEventTime("18:30:00Z");
            validDto.setEventTimeZone("Pacific Standard Time");
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
            validDto.setLanguageCode(LanguageOptions.English);

        //creating test user
        Users testUsers = new Users();
            testUsers.setUserId(1L);
            testUsers.setUsername("TestUser");

        //returning new entities from lookup service for test only
        when(mapperLookupService.findUserById(validDto.getUserId())).thenReturn(testUsers);

        GroupListing mappedEntity = new GroupListing();
        //setting groupId to imitate autogenerate from the database
            mappedEntity.setGroupId(1L);
            mappedEntity.setUsers(mapperLookupService.findUserById(validDto.getUserId()));
            mappedEntity.setListingTitle(validDto.getListingTitle());

        when(groupListingConversionService.convertToEntity(validDto)).thenReturn(mappedEntity);
        when(groupListingRepository.save(any(GroupListing.class))).thenReturn(mappedEntity);

        //when
        ResponseEntity<?> response = groupListingService.createGroupListing(validDto, testUsers);

        //then
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertAll("successful create listing assertions set: ",
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "createGrouplisting should not produce any error logs."),
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("This is a valid listing title",  responseBody.get("listingTitle")),
                () -> verify(groupListingConversionService, times(1)).convertToEntity(validDto),
                () -> verify(groupListingRepository, times(1)).save(any(GroupListing.class)),
                () -> verify(mapperLookupService, times(1)).findUserById(validDto.getUserId()),
                () -> verifyNoMoreInteractions(createGroupListingModelMapper, groupListingRepository));
    }


    //testing http response from unsuccessful create listing
    @Test
    void testCreateGroupListing_Fail() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
        //given
        CreateGroupListingDto invalidDto = new CreateGroupListingDto();

        //creating test user
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");

        //when
        ResponseEntity<?> response = groupListingService.createGroupListing(invalidDto, mockUser);

        //then
        assertAll("create listing failed assertions set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("CreateGroupListing failed. Reason: "))),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals("An error occurred while creating your listing.", response.getBody()),
                () -> verify(groupListingConversionService, times(1)).convertToEntity(invalidDto));
    }

    @Test
    void testUpdateGroupListing_Success() {
        // given
        Users owner = new Users();
        owner.setUserId(1L);
        owner.setUsername("Owner");

        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(1L);
        dto.setListingTitle("Updated title");

        GroupListing existingListing = new GroupListing();
        existingListing.setGroupId(1L);
        existingListing.setUsers(owner);
        existingListing.setListingTitle("Old title");

        GroupListing converted = new GroupListing();
        converted.setListingTitle("Updated title");

        when(groupListingRepository.findById(1L)).thenReturn(Optional.of(existingListing));
        when(groupListingConversionService.convertToEntity(any(UpdateGroupListingDto.class))).thenReturn(converted);
        when(groupListingRepository.save(any(GroupListing.class))).thenReturn(existingListing);
        when(notificationOutboxRepository.deleteOutboxNotificationsOnEntityUpdate(
                anyLong(), anyLong(), any(String.class))).thenReturn(0);

        // when
        ResponseEntity<?> response = groupListingService.updateGroupListing(dto, owner);

        // then
        assertAll("updateGroupListing success assertions:",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertInstanceOf(Map.class, response.getBody()),
                () -> assertEquals("Updated title", ((Map<?, ?>) response.getBody()).get("listingTitle")),
                () -> verify(groupListingRepository, times(1)).findById(1L),
                () -> verify(groupListingRepository, times(1)).save(any(GroupListing.class))
        );
    }

    @Test
    void testUpdateGroupListing_NotFound() {
        // given
        Users requestingUser = new Users();
        requestingUser.setUserId(1L);

        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(999L);

        when(groupListingRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> response = groupListingService.updateGroupListing(dto, requestingUser);

        // then
        assertAll("updateGroupListing not found assertions:",
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> verify(groupListingRepository, never()).save(any(GroupListing.class))
        );
    }

    @Test
    void testUpdateGroupListing_Unauthorized() {
        // given — listing is owned by a different user
        Users listingOwner = new Users();
        listingOwner.setUserId(99L);

        Users requestingUser = new Users();
        requestingUser.setUserId(1L);

        GroupListing existingListing = new GroupListing();
        existingListing.setGroupId(1L);
        existingListing.setUsers(listingOwner);

        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        dto.setGroupId(1L);

        when(groupListingRepository.findById(1L)).thenReturn(Optional.of(existingListing));

        // when
        ResponseEntity<?> response = groupListingService.updateGroupListing(dto, requestingUser);

        // then
        assertAll("updateGroupListing unauthorized assertions:",
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> verify(groupListingRepository, never()).save(any(GroupListing.class))
        );
    }

    @Test
    void testDeleteGroupListing_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);

        // given
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setGroupListings(new HashSet<>());

        GroupListing mockListing = new GroupListing();
        mockListing.setGroupId(1L);
        mockListing.setUsers(mockUser);  // same instance — Objects.equals returns true

        when(groupListingRepository.findById(1L)).thenReturn(Optional.of(mockListing));
        doNothing().when(archiveService).prepareUserDeleteRecords(any(), any());
        // userRepository.save and groupListingRepository.delete are void mocks (no-op by default)

        // when
        ResponseEntity<?> response = groupListingService.deleteGroupListing(1L, mockUser);

        // then
        assertAll("deleteGroupListing success assertions:",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(),
                        "Successful delete should return 200 OK"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(),
                        "Successful delete should not produce error logs"),
                () -> verify(groupListingRepository, times(1)).findById(1L),
                () -> verify(archiveService, times(1)).prepareUserDeleteRecords(any(), any()),
                () -> verify(groupListingRepository, times(1)).delete(any(GroupListing.class))
        );
    }

    @Test
    void testDeleteGroupListing_Fail_ListingNotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);

        // given
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");

        when(groupListingRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> response = groupListingService.deleteGroupListing(999L, mockUser);

        // then
        assertAll("deleteGroupListing not found assertions:",
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                        "Missing listing should return 404 NOT FOUND"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("DeleteGroupListing failed.")),
                        "Expected error log for missing listing"),
                () -> verify(archiveService, never()).prepareUserDeleteRecords(any(), any())
        );
    }

    @Test
    void testDeleteGroupListing_Fail_NotAuthorized() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);

        // given — listing is owned by a different user
        Users listingOwner = new Users();
        listingOwner.setUserId(99L);
        listingOwner.setUsername("ListingOwner");

        Users requestingUser = new Users();
        requestingUser.setUserId(1L);
        requestingUser.setUsername("DifferentUser");

        GroupListing mockListing = new GroupListing();
        mockListing.setGroupId(1L);
        mockListing.setUsers(listingOwner);  // owned by someone else

        when(groupListingRepository.findById(1L)).thenReturn(Optional.of(mockListing));

        // when
        ResponseEntity<?> response = groupListingService.deleteGroupListing(1L, requestingUser);

        // then
        assertAll("deleteGroupListing unauthorized assertions:",
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                        "Unauthorized delete should return 401"),
                () -> verify(archiveService, never()).prepareUserDeleteRecords(any(), any())
        );
    }

    @Test
    void testGetGroupListingById_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
        //given: dto with a valid and existing Id
        GroupListingResponseDto mockDto = new GroupListingResponseDto();
            mockDto.setGroupId(2L);

        //and group listing entities in repository
        GroupListing mockEntity1 = new GroupListing();
            mockEntity1.setGroupId(1L);
        GroupListing mockEntity2 = new GroupListing();
            mockEntity2.setGroupId(2L);
        when(groupListingRepository.findById(mockDto.getGroupId())).thenReturn(Optional.of(mockEntity2));
        when(groupListingConversionService.convertListingToResponseDto(mockEntity2)).thenReturn(mockDto);

        //when
        GroupListingResponseDto response = groupListingService.getGroupListingById(mockDto.getGroupId());

        //then
        assertAll("GetGroupListingById_Success assertion set: ",
                () -> assertNotNull(response, "Successful getGroupListingById should not return a null " +
                        "entity"),
                () -> assertDoesNotThrow(() -> groupListingRepository.findById(mockDto.getGroupId()),
                        "Successful getGroupListingById should not throw an exception."),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "getGroupListingById should not produce any error logs."),
                () -> assertEquals(mockEntity2.getGroupId(), response.getGroupId(), "getGroupListingById " +
                        "produced a Dto with the incorrect Id."),
                () -> verify(groupListingRepository, times(2)).findById(mockDto.getGroupId()));
    }

    @Test
    void testGetGroupListingById_Fail() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupListingServiceImpl.class);
        //given: a dto with an id that does not exist in repo
        GroupListingResponseDto mockDto = new GroupListingResponseDto();
            mockDto.setGroupId(1L);

        //when
        //then
        assertAll("GetGroupListingById_Fail assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        groupListingService.getGroupListingById(mockDto.getGroupId()), "getGroupListingById " +
                        "should throw an exception when the Id is not found."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("GetGroupListingById failed to find an entity with the " +
                                "given group Id: "))));

    }

    // --- searchGroupListings tests ---

    @Test
    void searchGroupListings_Anonymous_ReturnsPage() {
        // given
        SearchListingsDto dto = new SearchListingsDto(); // all nulls — no filters
        Pageable pageable = PageRequest.of(0, 10);

        GroupListing entity = new GroupListing();
        entity.setGroupId(1L);
        entity.setListingTitle("Test title");

        GroupListingResponseDto responseDto = new GroupListingResponseDto();
        responseDto.setGroupId(1L);
        responseDto.setListingTitle("Test title");

        when(groupListingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(groupListingConversionService.convertListingToResponseDto(entity)).thenReturn(responseDto);

        // when
        Page<GroupListingResponseDto> result = groupListingService.searchGroupListings(dto, pageable, Optional.empty());

        // then
        assertAll("searchGroupListings anonymous assertions:",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals("Test title", result.getContent().get(0).getListingTitle()),
                () -> verify(groupListingRepository).findAll(any(Specification.class), any(Pageable.class))
        );
    }

    @Test
    void searchGroupListings_Authenticated_ReturnsPage() {
        // given
        SearchListingsDto dto = new SearchListingsDto(); // all nulls — no filters
        Pageable pageable = PageRequest.of(0, 10);

        Users user = new Users();
        user.setUserId(1L);

        GroupListing entity = new GroupListing();
        entity.setGroupId(1L);
        entity.setListingTitle("Auth test title");

        GroupListingResponseDto responseDto = new GroupListingResponseDto();
        responseDto.setGroupId(1L);
        responseDto.setListingTitle("Auth test title");

        when(groupListingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(groupListingConversionService.convertListingToResponseDto(entity)).thenReturn(responseDto);

        // when
        Page<GroupListingResponseDto> result = groupListingService.searchGroupListings(dto, pageable, Optional.of(user));

        // then
        assertAll("searchGroupListings authenticated assertions:",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals("Auth test title", result.getContent().get(0).getListingTitle()),
                () -> verify(groupListingRepository).findAll(any(Specification.class), any(Pageable.class))
        );
    }

}