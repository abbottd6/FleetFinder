package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupListingConversionServiceImplTest {

    // Three separate mocks are required because all three are the same type (ModelMapper)
    // and Mockito cannot distinguish them by type alone for @InjectMocks.
    @Mock
    private ModelMapper createMapper;

    @Mock
    private ModelMapper updateMapper;

    @Mock
    private ModelMapper responseMapper;

    private GroupListingConversionServiceImpl service;

    @BeforeEach
    void setUp() {
        // Constructor order: createGroupListingModelMapper, updateGroupListingModelMapper, groupListingResponseDtoMapper
        service = new GroupListingConversionServiceImpl(createMapper, updateMapper, responseMapper);
    }

    @Test
    void convertListingToResponseDto_MapsEntityToResponseDto() {
        GroupListing listing = new GroupListing();
        GroupListingResponseDto expectedDto = new GroupListingResponseDto();
        when(responseMapper.map(listing, GroupListingResponseDto.class)).thenReturn(expectedDto);

        GroupListingResponseDto result = service.convertListingToResponseDto(listing);

        assertThat(result).isEqualTo(expectedDto);
        verify(responseMapper).map(listing, GroupListingResponseDto.class);
    }

    @Test
    void convertToEntity_FromCreateDto_MapsToGroupListing() {
        CreateGroupListingDto dto = new CreateGroupListingDto();
        GroupListing expectedEntity = new GroupListing();
        when(createMapper.map(dto, GroupListing.class)).thenReturn(expectedEntity);

        GroupListing result = service.convertToEntity(dto);

        assertThat(result).isEqualTo(expectedEntity);
        verify(createMapper).map(dto, GroupListing.class);
    }

    @Test
    void convertToEntity_FromUpdateDto_MapsToGroupListing() {
        UpdateGroupListingDto dto = new UpdateGroupListingDto();
        GroupListing expectedEntity = new GroupListing();
        when(updateMapper.map(dto, GroupListing.class)).thenReturn(expectedEntity);

        GroupListing result = service.convertToEntity(dto);

        assertThat(result).isEqualTo(expectedEntity);
        verify(updateMapper).map(dto, GroupListing.class);
    }
}
