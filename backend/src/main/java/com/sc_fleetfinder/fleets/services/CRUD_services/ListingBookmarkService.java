package com.sc_fleetfinder.fleets.services.CRUD_services;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface ListingBookmarkService {

    ListingBookmarkDto getBookmarkById(Long id);
    Set<ListingBookmarkDto> getBookmarksByUserId(Long userId);
    List<ListingBookmarkDto> getBookmarksByListingId(Long listingId);
    ResponseEntity<?> addBookmark(AddBookmarkRequestDto dto);
    ResponseEntity<?> deleteBookmarkById(DeleteBookmarkRequestDto dto);
}
