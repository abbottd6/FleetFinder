package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.ListingBookmarkRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.services.conversion_services.BookmarkConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkConversionServiceImplTest {

    @Mock
    private ListingBookmarkRepository bookmarkRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookmarkConversionServiceImpl bookmarkConversionService;

    @Test
    void convertToDto_MapsBookmarkToDto() {
        // ListingBookmark no-arg constructor is protected; use mock to avoid access restriction
        ListingBookmark bookmark = mock(ListingBookmark.class);
        ListingBookmarkDto expectedDto = new ListingBookmarkDto();
        when(modelMapper.map(bookmark, ListingBookmarkDto.class)).thenReturn(expectedDto);

        ListingBookmarkDto result = bookmarkConversionService.convertToDto(bookmark);

        assertThat(result).isEqualTo(expectedDto);
        verify(modelMapper).map(bookmark, ListingBookmarkDto.class);
    }
}
