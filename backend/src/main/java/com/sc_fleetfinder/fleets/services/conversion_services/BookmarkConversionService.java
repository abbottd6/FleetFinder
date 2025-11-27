package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import jakarta.validation.Valid;

public interface BookmarkConversionService {

    ListingBookmarkDto convertToDto(@Valid ListingBookmark bookmark);
    ListingBookmark convertToEntity(@Valid AddBookmarkRequestDto addBookmarkRequestDto);
}
