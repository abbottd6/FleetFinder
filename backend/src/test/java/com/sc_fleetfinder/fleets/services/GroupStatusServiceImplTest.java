package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
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

    @InjectMocks
    private GroupStatusServiceImpl groupStatusService;

    @Test
    void testGetAllGroupStatuses_Found() {
        //given
        GroupStatus mockStatus1 = new GroupStatus();
        GroupStatus mockStatus2 = new GroupStatus();
        List<GroupStatus> groupStatuses = List.of(mockStatus1, mockStatus2);
        when(groupStatusRepository.findAll()).thenReturn(groupStatuses);

        //when
        List<GroupStatusDto> result = groupStatusService.getAllGroupStatuses();

        //then
        assertAll("groupStatus get all mock entities assertion set:",
                () -> assertNotNull(result, "get all groupStatuses should not be null"),
                () -> assertEquals(2, result.size(), "get all groupStatuses should return a list " +
                        "containing 2 mock entities"),
                () -> verify(groupStatusRepository, times(1)).findAll());
    }

    @Test
    void testGetAllGroupStatuses_NotFound() {
        //given
        when(groupStatusRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GroupStatusDto> result = groupStatusService.getAllGroupStatuses();

        //then
        assertAll("get all groupStatuses is empty, assertion set:",
                () -> assertNotNull(result, "get all groupStatuses should be empty, not null"),
                () -> assertTrue(result.isEmpty(), "get all groupStatuses should return empty list"),
                () -> verify(groupStatusRepository, times(1)).findAll());
    }

    @Test
    void testGetGroupStatusById_Found() {
        //given
        GroupStatus mockStatus1 = new GroupStatus();
        mockStatus1.setGroupStatusId(1);
        when(groupStatusRepository.findById(1)).thenReturn(Optional.of(mockStatus1));

        //when
        GroupStatusDto result = groupStatusService.getGroupStatusById(1);

        //then
        assertAll("get groupStatus by Id=found assertion set:",
                () -> assertNotNull(result, "get groupStatus by id=found should not return null"),
                () -> assertDoesNotThrow(() -> groupStatusService.getGroupStatusById(1),
                        "get groupStatusById should not throw an exception when Id is found"),
                () -> verify(groupStatusRepository, times(2)).findById(1));
    }

    @Test
    void testGetGroupStatusById_NotFound() {
        //given
        when(groupStatusRepository.findById(1)).thenReturn(Optional.empty());

        //when groupStatusRepository does not contain entity with given id

        //then
        assertThrows(ResourceNotFoundException.class, () -> groupStatusService.getGroupStatusById(1),
                "get groupStatusById should throw an exception when Id is NOT found");
    }
}