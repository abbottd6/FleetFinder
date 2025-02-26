package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupStatusConversionServiceImplTest {

    @InjectMocks
    private GroupStatusConversionServiceImpl groupStatusConversionService;

    @Mock
    private GroupStatusRepository groupStatusRepository;

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
        List<GroupStatusDto> result = List.of(groupStatusConversionService.convertToDto(mockEntity1),
                groupStatusConversionService.convertToDto(mockEntity2));

        //then
        assertAll("convert group status entity to Dto assertion set: ",
                () -> assertNotNull(result, "group status convertToDto should not return null dto's"),
                () -> assertDoesNotThrow(() -> groupStatusConversionService.convertToDto(mockEntity1),
                        "group status convertToDto should NOT throw an exception when fields are valid."),
                () -> assertDoesNotThrow(() -> groupStatusConversionService.convertToDto(mockEntity2),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToDto(mockEntity),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToDto(mockEntity),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToDto(mockEntity),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToDto(mockEntity),
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
        GroupStatus result = groupStatusConversionService.convertToEntity(mockDto);

        //then
        assertAll("groupStatus convertToEntity assertion set: ",
                () -> assertNotNull(result, "groupStatus convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> groupStatusConversionService.convertToEntity(mockDto),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToEntity(mockDto),
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
        assertThrows(ResourceNotFoundException.class, () -> groupStatusConversionService.convertToEntity(mockDto));
    }
}