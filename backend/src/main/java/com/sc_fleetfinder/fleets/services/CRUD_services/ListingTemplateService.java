package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrEditListingTemplateDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ListingTemplateService {

    Page<ListingTemplateResponseDto> getMyTemplates(Users user, Pageable pageable);
    ResponseEntity<?> createTemplate(Users user, CreateOrEditListingTemplateDto dto);
    ResponseEntity<?> removeTemplate(Users user, Long templateId);
}
