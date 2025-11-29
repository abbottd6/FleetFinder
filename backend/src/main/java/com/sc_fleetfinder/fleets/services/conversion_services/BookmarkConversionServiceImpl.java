package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ListingBookmarkRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingBookmarkDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
public class BookmarkConversionServiceImpl implements BookmarkConversionService {

    private final ListingBookmarkRepository bmr;
    private final ModelMapper modelMapper;


    public BookmarkConversionServiceImpl(ListingBookmarkRepository bookmarkRepository,
                                         ModelMapper modelMapper) {
        this.bmr = bookmarkRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ListingBookmarkDto convertToDto(@Valid ListingBookmark bookmark) {
        return modelMapper.map(bookmark, ListingBookmarkDto.class);
    }

    @Override
    public ListingBookmark convertToEntity(@Valid AddBookmarkRequestDto dto) {

        ListingBookmark bookmark = new ListingBookmark();

        bookmark.setUser(dto.getUser());
        bookmark.setGroup(dto.getGroup());

        return bookmark;
    }
}
