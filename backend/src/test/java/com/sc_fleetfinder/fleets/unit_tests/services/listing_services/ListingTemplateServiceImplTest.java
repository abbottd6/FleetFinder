package com.sc_fleetfinder.fleets.unit_tests.services.listing_services;

import com.sc_fleetfinder.fleets.DAO.ListingTemplateRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrEditListingTemplateDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingTemplateServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.TemplateConversionService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingTemplateServiceImplTest {

    @Mock
    private ListingTemplateRepository ltr;

    @Mock
    private TemplateConversionService tcs;

    @InjectMocks
    private ListingTemplateServiceImpl listingTemplateService;

    private Users mockUser;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("test@gmail.com");
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getMyTemplates_ReturnsPageOfDtos_WhenTemplatesExist() {
        ListingTemplate entity1 = new ListingTemplate();
        ListingTemplate entity2 = new ListingTemplate();
        ListingTemplateResponseDto dto1 = new ListingTemplateResponseDto();
        dto1.setTemplateId(1L);
        ListingTemplateResponseDto dto2 = new ListingTemplateResponseDto();
        dto2.setTemplateId(2L);

        when(ltr.getListingTemplatesByUser(mockUser, pageable))
                .thenReturn(new PageImpl<>(List.of(entity1, entity2)));
        when(tcs.convertToDto(entity1)).thenReturn(dto1);
        when(tcs.convertToDto(entity2)).thenReturn(dto2);

        Page<ListingTemplateResponseDto> result = listingTemplateService.getMyTemplates(mockUser, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(tcs, times(2)).convertToDto(any());
    }

    @Test
    void getMyTemplates_ReturnsEmptyPage_WhenNoTemplatesExist() {
        when(ltr.getListingTemplatesByUser(mockUser, pageable))
                .thenReturn(Page.empty());

        Page<ListingTemplateResponseDto> result = listingTemplateService.getMyTemplates(mockUser, pageable);

        assertThat(result.isEmpty()).isTrue();
        verify(tcs, never()).convertToDto(any());
    }

    @Test
    void createTemplate_Success_ShortTitle() {
        String shortTitle = "Short title";
        CreateOrEditListingTemplateDto dto = new CreateOrEditListingTemplateDto();
        ListingTemplate mockTemplate = new ListingTemplate();
        mockTemplate.setListingTitle(shortTitle);

        when(tcs.convertToEntity(any())).thenReturn(mockTemplate);
        when(ltr.save(any())).thenReturn(mockTemplate);

        ResponseEntity<?> result = listingTemplateService.createTemplate(mockUser, dto);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("message")).isEqualTo("Template saved: Short title");
        verify(ltr).save(any());
    }

    @Test
    void createTemplate_Success_LongTitle_Truncated() {
        // title > 25 chars — service truncates at 25 + "..."
        String longTitle = "This title is longer than twenty-five characters";
        CreateOrEditListingTemplateDto dto = new CreateOrEditListingTemplateDto();
        ListingTemplate mockTemplate = new ListingTemplate();
        mockTemplate.setListingTitle(longTitle);

        when(tcs.convertToEntity(any())).thenReturn(mockTemplate);
        when(ltr.save(any())).thenReturn(mockTemplate);

        ResponseEntity<?> result = listingTemplateService.createTemplate(mockUser, dto);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("message")).isEqualTo("Template saved: This title is longer than...");
        verify(ltr).save(any());
    }

    @Test
    void createTemplate_Fail_ExceptionThrown() {
        CreateOrEditListingTemplateDto dto = new CreateOrEditListingTemplateDto();
        when(tcs.convertToEntity(any())).thenThrow(new RuntimeException("Conversion failed"));

        try (LogCaptor logCaptor = LogCaptor.forClass(ListingTemplateServiceImpl.class)) {
            ResponseEntity<?> result = listingTemplateService.createTemplate(mockUser, dto);

            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) result.getBody();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            // exact typo in source: "Could note create the listing template."
            assertThat(body.get("message")).contains("Could note create the listing template.");
            assertThat(logCaptor.getErrorLogs())
                    .anyMatch(msg -> msg.contains("Could not create listing template."));
            verify(ltr, never()).save(any());
        }
    }

    @Test
    void removeTemplate_Success_Deleted() {
        when(ltr.deleteByUserAndId(mockUser, 1L)).thenReturn(1);

        ResponseEntity<?> result = listingTemplateService.removeTemplate(mockUser, 1L);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("message")).isEqualTo("Template deleted.");
    }

    @Test
    void removeTemplate_Success_NotFound() {
        // Returns 200 (not 404) when no rows matched — key difference from GroupListing
        when(ltr.deleteByUserAndId(mockUser, 999L)).thenReturn(0);

        ResponseEntity<?> result = listingTemplateService.removeTemplate(mockUser, 999L);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("message")).isEqualTo("Template not found");
    }

    @Test
    void removeTemplate_Fail_ExceptionThrown() {
        when(ltr.deleteByUserAndId(any(), any())).thenThrow(new RuntimeException("DB error"));

        try (LogCaptor logCaptor = LogCaptor.forClass(ListingTemplateServiceImpl.class)) {
            ResponseEntity<?> result = listingTemplateService.removeTemplate(mockUser, 1L);

            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) result.getBody();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(body.get("message")).isEqualTo("There was an error deleting this template.");
            assertThat(logCaptor.getErrorLogs())
                    .anyMatch(msg -> msg.contains("Template could not be deleted:"));
        }
    }
}
