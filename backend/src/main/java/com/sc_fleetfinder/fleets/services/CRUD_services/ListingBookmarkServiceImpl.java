package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ListingBookmarkRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.BookmarkConversionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class ListingBookmarkServiceImpl implements ListingBookmarkService {

    private final ListingBookmarkRepository bmr;
    private final BookmarkConversionService bcs;
    private final GroupListingRepository glr;
    private final UserRepository userRepo;

    public ListingBookmarkServiceImpl(ListingBookmarkRepository bookmarkRepository,
                                      BookmarkConversionService bookmarkConversionService,
                                      GroupListingRepository glr,
                                      UserRepository userRepo) {
        this.bmr = bookmarkRepository;
        this.bcs = bookmarkConversionService;
        this.glr = glr;
        this.userRepo = userRepo;
    }

    @Override
    public ListingBookmarkDto getBookmarkById(Long id) {
        ListingBookmark bookmark = bmr.findById(id)
                .orElseThrow(() -> {
                    log.error("Bookmark not found for id {}", id);
                    return new ResourceNotFoundException(id);
                });
        return bcs.convertToDto(bookmark);
    }

    @Override
    public ResponseEntity<?> getBookmarksByUserId(Long userId) {
        try {
            Users user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Users", userId));

            Set<ListingBookmarkDto> bookmarks = bmr.findByUser(user)
                    .stream()
                    .map(bcs::convertToDto)
                    .collect(Collectors.toSet());

            return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
        }
        catch (ResourceNotFoundException e) {
            log.error("Cannot retrieve bookmarks. User not found for id {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> getBookmarkBriefByUserId(Long userId) {
        try {
            Users user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Users", userId));

            Set<Long> bmBrief = bmr.findByUser(user).stream()
                    .map(bm -> bm.getGroup().getGroupId())
                    .collect(Collectors.toSet());

            return ResponseEntity.status(HttpStatus.OK).body(bmBrief);
        }
        catch (ResourceNotFoundException e) {
            log.error("Cannot retrieve Bookmarks Brief. User not found for id {}", userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Override
    public List<ListingBookmarkDto> getBookmarksByListingId(Long groupId) {

        try {
            GroupListing listing = glr.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupListing", groupId));

            return bmr.findByGroup(listing)
                    .stream()
                    .map(bcs::convertToDto)
                    .collect(Collectors.toList());
        }

        //TO DO: not sure what I want this to do yet. Should probably be used when deleting a listing
        // to delete all the associated bookmarks.
        catch (Exception e){
            log.error("GroupListing not found for id {}", groupId);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> addBookmark(Long groupId, Users user) {
        try {
            GroupListing listing = glr.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupListing", groupId));

            if (bmr.findByUserAndGroup(user, listing).isEmpty()) {

                ListingBookmark bookmark = new ListingBookmark(listing, user);
                bmr.save(bookmark);

                String title = listing.getListingTitle();
                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", title.length() <= 25 ? title : title.substring(0, 25) + "...");

                return ResponseEntity.status(HttpStatus.CREATED).body(response);

            } else {

                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", "Already bookmarked");

                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
            }
        } catch (Exception e) {
            log.error("AddBookmark failed. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding this bookmark.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteBookmarkById(Long groupId, Users user) {
        try {
            GroupListing group = glr.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupListing", groupId));
            ListingBookmark bm = bmr.findByUserAndGroup(user, group)
                    .orElseThrow(() -> new ResourceNotFoundException("Bookmark", user.getUserId(), groupId));

            bmr.deleteById(bm.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bookmark removed.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("DeleteBookmark failed. {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "An error occurred while deleting this bookmark.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
