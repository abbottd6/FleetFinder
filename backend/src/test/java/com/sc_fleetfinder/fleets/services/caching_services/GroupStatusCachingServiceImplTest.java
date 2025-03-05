package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupStatusConversionServiceImpl;
import nl.altindag.log.LogCaptor;
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
class GroupStatusCachingServiceImplTest {

    @Mock
    private GroupStatusRepository groupStatusRepository;

    @Mock
    private GroupStatusConversionServiceImpl groupStatusConversionService;

    @InjectMocks
    private GroupStatusCachingServiceImpl groupStatusCachingService;

    @Test
    void testCacheAllGroupStatuses_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupStatusCachingServiceImpl.class);
        //given
        //mock entities for find all
        GroupStatus mockEntity1 = new GroupStatus();
            mockEntity1.setGroupStatusId(1);
            mockEntity1.setGroupStatus("Test1");
        GroupStatus mockEntity2 = new GroupStatus();
            mockEntity2.setGroupStatusId(2);
            mockEntity2.setGroupStatus("Test2");
        List<GroupStatus> groupStatuses = List.of(mockEntity1, mockEntity2);
        when(groupStatusRepository.findAll()).thenReturn(groupStatuses);

        //mock dto's to return from conversion in cacheAll
        GroupStatusDto mockDto1 = new GroupStatusDto();
            mockDto1.setGroupStatusId(1);
            mockDto1.setGroupStatus("Test1");
        GroupStatusDto mockDto2 = new GroupStatusDto();
            mockDto2.setGroupStatusId(2);
            mockDto2.setGroupStatus("Test2");
        when(groupStatusConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(groupStatusConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<GroupStatusDto> result = groupStatusCachingService.cacheAllGroupStatuses();

        //then
        assertAll("cacheAllGroupStatuses mock entities assertion set:",
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "cacheAllGroupStatuses should not produce any error logs."),
                () -> assertNotNull(result, "get all groupStatuses should not return null"),
                () -> assertEquals(2, result.size(), "get all groupStatuses should return a list " +
                        "containing 2 mock entities"),
                () -> assertEquals(1, result.getFirst().getGroupStatusId(), "cacheAllGroupStatuses " +
                        "produced dto with incorrect id"),
                () -> assertEquals("Test1", result.getFirst().getGroupStatus(), "cacheAllGroupStatuses " +
                        "produced dto with incorrect status qualifier"),
                () -> assertEquals(2, result.get(1).getGroupStatusId(), "cacheAllGroupStatsuses " +
                        "produced dto with incorrect id"),
                () -> assertEquals("Test2", result.get(1).getGroupStatus(),
                        "cacheAllGroupStatuses produced dto with incorrect status qualifier"),
                () -> verify(groupStatusRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllGroupStatuses_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GroupStatusCachingServiceImpl.class);
        //given group status repo is empty

        //when
        //then
        assertAll("cacheAllGroupStatuses = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        groupStatusCachingService.cacheAllGroupStatuses()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access Group Status data for caching."))),
                () -> verify(groupStatusRepository, times(1)).findAll());
    }
}