package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupStatusServiceImpl;
import com.sc_fleetfinder.fleets.services.caching_services.GroupStatusCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
}