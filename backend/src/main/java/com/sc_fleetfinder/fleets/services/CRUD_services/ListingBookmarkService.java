package com.sc_fleetfinder.fleets.services.CRUD_services;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface ListingBookmarkService {

    ListingBookmarkDto getBookmarkById(Long id);
    ResponseEntity<?> getBookmarksByUserId(Long userId, GenericPageRequestDto pageDto);
    ResponseEntity<?> getBookmarkBriefByUserId(Long userId);
    List<ListingBookmarkDto> getBookmarksByListingId(Long listingId);
    ResponseEntity<?> addBookmark(Long groupId, Users user);
    ResponseEntity<?> deleteBookmarkById(Long groupId, Users user);
    ResponseEntity<?> deleteMultipleBookmarks(Users user, Set<Long> groupIds);
}
