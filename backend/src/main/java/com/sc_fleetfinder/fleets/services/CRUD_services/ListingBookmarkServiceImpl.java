package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ListingBookmarkRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteBookmarkRequestDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class ListingBookmarkServiceImpl implements ListingBookmarkService {

    private final ListingBookmarkRepository bmr;
    private final BookmarkConversionService bcs;
    private final GroupListingRepository glr;

    public ListingBookmarkServiceImpl(ListingBookmarkRepository bookmarkRepository,
                                      BookmarkConversionService bookmarkConversionService,
                                      GroupListingRepository glr) {
        this.bmr = bookmarkRepository;
        this.bcs = bookmarkConversionService;
        this.glr = glr;
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
    public Set<ListingBookmarkDto> getBookmarksByUserId(Long userId) {

        return bmr.findByUserId(userId)
                .stream()
                .map(bcs::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public List<ListingBookmarkDto> getBookmarksByListingId(Long listingId) {

        return bmr.findByListingId(listingId)
                .stream()
                .map(bcs::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<?> addBookmark(AddBookmarkRequestDto dto) {
        Objects.requireNonNull(dto, "AddBookmarkRequestDto cannot be null");
            try {
                GroupListing entity = glr.findById(dto.getGroupId())
                        .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getGroupId()));

                ListingBookmark bookmark = bcs.convertToEntity(dto);

                bmr.save(bookmark);

                String title = entity.getListingTitle();
                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", title.length() <= 15 ? title : title.substring(0, 15) + "...");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            catch (Exception e) {
                log.error("AddBookmark failed. {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while adding this bookmark.");
            }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteBookmarkById(DeleteBookmarkRequestDto dto) {
        Objects.requireNonNull(dto, "DeleteBookmarkRequestDto cannot be null");

        try {
            bmr.findById(dto.getBookmarkId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bookmark", dto.getBookmarkId()));

            bmr.deleteById(dto.getBookmarkId());
            String response = "Bookmark removed successfully";
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("DeleteBookmark failed. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting this bookmark.");
        }
    }
}
