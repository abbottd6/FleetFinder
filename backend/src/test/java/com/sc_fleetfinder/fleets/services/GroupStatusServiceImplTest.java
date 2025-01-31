package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.GroupStatusCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupStatusServiceImplTest {

    @Mock
    private GroupStatusRepository groupStatusRepository;

    @Mock
    private GroupStatusCachingServiceImpl groupStatusCachingService;

    @InjectMocks
    private GroupStatusServiceImpl groupStatusService;

    @Test
    void testGetAllGroupStatuses_Found() {
        //given
        GroupStatusDto mockDto1 = new GroupStatusDto();
        GroupStatusDto mockDto2 = new GroupStatusDto();
        List<GroupStatusDto> groupStatuses = List.of(mockDto1, mockDto2);
        when(groupStatusCachingService.cacheAllGroupStatuses()).thenReturn(groupStatuses);

        //when
        List<GroupStatusDto> result = groupStatusService.getAllGroupStatuses();

        //then
        assertAll("getAllGroupStatuses mock entities assertion set:",
                () -> assertNotNull(result, "get all groupStatuses should not return null"),
                () -> assertEquals(2, result.size(), "get all groupStatuses should return a list " +
                        "containing 2 mock entities"),
                () -> verify(groupStatusCachingService, times(1)).cacheAllGroupStatuses());
    }

    @Test
    void testGetAllGroupStatuses_NotFound() {
        //given: group status repo is empty

        //telling test to throw an exception when it tires to cache an empty repo
        when(groupStatusCachingService.cacheAllGroupStatuses()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllGroupStatuses = empty, assertion set:",
                //verifying that the exception propagates from the caching service
                () -> assertThrows(ResourceNotFoundException.class, () -> groupStatusService.getAllGroupStatuses()),
                () -> verify(groupStatusCachingService, times(1)).cacheAllGroupStatuses());
    }

    @Test
    void testGetGroupStatusById_Found() {
        //given
        GroupStatusDto mockDto1 = new GroupStatusDto();
            mockDto1.setGroupStatusId(1);
            mockDto1.setGroupStatus("Test1");
        GroupStatusDto mockDto2 = new GroupStatusDto();
            mockDto2.setGroupStatusId(2);
            mockDto2.setGroupStatus("Test2");
        List<GroupStatusDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(groupStatusCachingService.cacheAllGroupStatuses()).thenReturn(mockDtoes);

        //when
        GroupStatusDto result = groupStatusService.getGroupStatusById(1);

        //then
        assertAll("getGroupStatusById = found assertion set:",
                () -> assertNotNull(result, "get groupStatus by id=found should not return null"),
                () -> assertDoesNotThrow(() -> groupStatusService.getGroupStatusById(1),
                        "get groupStatusById should not throw an exception when Id is found"),
                () -> verify(groupStatusCachingService, times(2)).cacheAllGroupStatuses());
    }

    @Test
    void testGetGroupStatusById_NotFound() {
        //given: group status repository does not contain group status with given id

        //when
        //then
        assertAll("getGroupStatusById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> groupStatusService.getGroupStatusById(1),
                "get groupStatusById should throw an exception when Id is NOT found"),
                () -> verify(groupStatusCachingService, times(1)).cacheAllGroupStatuses());
    }

    @Test
    void testConvertToDto_Success() {
        //given
        GroupStatus mockEntity1 = new GroupStatus();
            mockEntity1.setGroupStatusId(1);
            mockEntity1.setGroupStatus("Status1");
        GroupStatus mockEntity2 = new GroupStatus();
            mockEntity2.setGroupStatusId(2);
            mockEntity2.setGroupStatus("Status2");

        //when
        List<GroupStatusDto> result = List.of(groupStatusService.convertToDto(mockEntity1),
                groupStatusService.convertToDto(mockEntity2));

        //then
        assertAll("convert group status entity to Dto assertion set: ",
                () -> assertNotNull(result, "group status convertToDto should not return null dto's"),
                () -> assertDoesNotThrow(() -> groupStatusService.convertToDto(mockEntity1),
                        "group status convertToDto should NOT throw an exception when fields are valid."),
                () -> assertDoesNotThrow(() -> groupStatusService.convertToDto(mockEntity2),
                        "group status convertToDto should NOT throw an exception when fields are valid"),
                () -> assertEquals(2, result.size(), "test convertToDto for group status is expected " +
                        "to produce a list with 2 mock dtos"),
                () -> assertEquals(1, result.getFirst().getGroupStatusId(), "groupStatus convertToDto " +
                        " produced dto with incorrect id"),
                () -> assertEquals("Status1", result.getFirst().getGroupStatus(),
                        "groupStatus convertToDto produced dto with incorrect status qualifier"),
                () -> assertEquals(2, result.get(1).getGroupStatusId(),
                        "groupStatus convertToDto produced dto with incorrect id"),
                () -> assertEquals("Status2", result.get(1).getGroupStatus(),
                        "groupStatus convertToDto produced dto with incorrect status qualifier"));
    }

    @Test
    void testConvertToDto_FailNullId() {
        //given: entity with a null id value
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(null);
            mockEntity.setGroupStatus("Status1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToDto(mockEntity),
                "groupStatus convertToDto should throw an exception when the id is null");
    }

    @Test
    void testConvertToDto_FailIdZero() {
        //given: entity with an id value of 0
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(0);
            mockEntity.setGroupStatus("Status1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToDto(mockEntity),
                "groupStatus convertToDto should throw an exception when id value is 0");
    }

    @Test
    void testConvertToDto_FailQualifierIsNull() {
        //given: entity with null status qualifier
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(1);
            mockEntity.setGroupStatus(null);

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToDto(mockEntity),
                "groupStatus convertToDto should throw an exception when the status qualifier is null");
    }

    @Test
    void testConvertToDto_FailQualifierIsEmpty() {
        //given: entity with empty string for group status
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(1);
            mockEntity.setGroupStatus("");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToDto(mockEntity),
                "groupStatus convertToDto should throw an exception when the group status is an empty string");
    }

    @Test
    void testConvertToEntity_Found() {
        //given: a dto that matches an entity
        GroupStatusDto mockDto = new GroupStatusDto();
            mockDto.setGroupStatusId(1);
            mockDto.setGroupStatus("Status1");
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(1);
            mockEntity.setGroupStatus("Status1");

        when(groupStatusRepository.findById(mockDto.getGroupStatusId())).thenReturn(Optional.of(mockEntity));

        //when
        GroupStatus result = groupStatusService.convertToEntity(mockDto);

        //then
        assertAll("groupStatus convertToEntity assertion set: ",
                () -> assertNotNull(result, "groupStatus convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> groupStatusService.convertToEntity(mockDto),
                        "groupStatus convertToEntity with valid dto should not throw an exception"),
                () -> assertEquals(1, result.getGroupStatusId(), "groupStatus convertToEntity " +
                        " produces an incorrect id"),
                () -> assertEquals("Status1", result.getGroupStatus(), "groupStatus convertToEntity " +
                        "produces an incorrect status qualifier"),
                () -> assertSame(mockEntity, result, "groupStatus convertToEntity does not match " +
                        "target entity"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        //given: dto with an id that doesn't exist in repo
        GroupStatusDto mockDto = new GroupStatusDto();
            mockDto.setGroupStatusId(1);
            mockDto.setGroupStatus("Not a Status");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToEntity(mockDto),
                "groupStatus convertToEntity where dto id is not found in repo should throw an exception");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        //given: dto with a name that does not match the name of the entity with the same id
        GroupStatusDto mockDto = new GroupStatusDto();
            mockDto.setGroupStatusId(1);
            mockDto.setGroupStatus("Incorrect Status");
        GroupStatus mockEntity = new GroupStatus();
            mockEntity.setGroupStatusId(1);
            mockEntity.setGroupStatus("Correct Status");

        //when
        when(groupStatusRepository.findById(mockDto.getGroupStatusId())).thenReturn(Optional.of(mockEntity));

        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.convertToEntity(mockDto));
    }
}