package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.ListingTemplateRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.conversion_services.TemplateConversionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ListingTemplateServiceImpl implements ListingTemplateService {

    private final ListingTemplateRepository ltr;
    private final TemplateConversionService tcs;

    ListingTemplateServiceImpl(ListingTemplateRepository ltr,
                               TemplateConversionService tcs){
        this.ltr = ltr;
        this.tcs = tcs;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListingTemplateResponseDto> getMyTemplates(Users user, Pageable pageable) {
        return ltr.getListingTemplatesByUser(user, pageable).map(tcs::convertToDto);
    }

    @Override
    @Transactional
    public ResponseEntity<?> createTemplate(Users user, CreateGroupListingDto dto) {
        Map<String, String> response = new HashMap<>();

        try {
            dto.setUserId(user.getUserId());
            ListingTemplate template = tcs.convertToEntity(dto);

            ltr.save(template);

            String title = template.getListingTitle();

            response.put("message", "Template saved: " + (title.length() <= 25 ? title : title.substring(0, 25) + "..."));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            response.put("message", "Could note create the listing template." + e.getMessage());
            log.error("Could not create listing template. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> removeTemplate(Users user, Long templateId) {
        Map<String, String> response = new HashMap<>();

        try {
            int deleted = ltr.deleteByUserAndId(user, templateId);

            if(deleted > 0) {
                response.put("message", "Template deleted.");
            } else {
              response.put("message", "Template not found");
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("Template could not be deleted: {}", e.getMessage());
            response.put("message", "There was an error deleting this template.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
