package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrEditListingTemplateDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.UserController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.HiddenListingService;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingBookmarkService;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingTemplateService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.reporting_services.ListingReportingService;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ListingTemplateService lts;

    @MockitoBean
    private ListingBookmarkService bms;

    @MockitoBean
    private HiddenListingService hls;

    @MockitoBean
    private ListingReportingService lrs;

    @MockitoBean
    private UserActivityCache userActivityCache;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setKeycloakId("someKeycloakId");
        mockUser.setEmail("test@gmail.com");
        when(userService.verifyUser("someKeycloakId")).thenReturn(mockUser);
    }

    // ─── getTemplates ──────────────────────────────────────────────────────────

    @Test
    void getTemplates_Success_ReturnsPaginatedPage() throws Exception {
        ListingTemplateResponseDto dto = new ListingTemplateResponseDto();
        dto.setTemplateId(1L);
        Page<ListingTemplateResponseDto> mockPage = new PageImpl<>(List.of(dto));
        when(lts.getMyTemplates(any(), any())).thenReturn(mockPage);

        SortablePageRequestDto pageDto = new SortablePageRequestDto();
        pageDto.setPage(0);
        pageDto.setSize(10);

        mockMvc.perform(post("/api/users/my/templates/get")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].templateId").isNumber());
    }

    @Test
    void getTemplates_Unauthorized_NoJwt() throws Exception {
        mockMvc.perform(post("/api/users/my/templates/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── createTemplate ────────────────────────────────────────────────────────

    @Test
    void createTemplate_Success_AllFields() throws Exception {
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
        mockDto.setServerId(1);
        mockDto.setEnvironmentId(1);
        mockDto.setExperienceId(1);
        mockDto.setListingTitle("Test template title");
        mockDto.setPlayStyleId(1);
        mockDto.setLegalityId(1);
        mockDto.setGroupStatusId(1);
        mockDto.setEventDate("2025-03-15");
        mockDto.setEventTime("18:30:00Z");
        mockDto.setEventTimeZone("Pacific Standard Time");
        mockDto.setCategoryId(1);
        mockDto.setSubcategoryId(1);
        mockDto.setPvpStatusId(1);
        mockDto.setSystemId(1);
        mockDto.setPlanetId(1);
        mockDto.setListingDescription("Test template description");
        mockDto.setDesiredPartySize(2);
        mockDto.setCurrentPartySize(1);
        mockDto.setAvailableRoles("Test roles");
        mockDto.setCommsOption("Optional");
        mockDto.setCommsService("Discord");
        mockDto.setLanguageCode(LanguageOptions.English);

        doAnswer(inv -> ResponseEntity.ok(Map.of("message", "Template saved: Test template title")))
                .when(lts).createTemplate(any(Users.class), any(CreateOrEditListingTemplateDto.class));

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createTemplate_Success_RequiredFieldsOnly() throws Exception {
        // No @Valid on this endpoint — nullable fields are allowed as null
        CreateGroupListingDto mockDto = new CreateGroupListingDto();
        mockDto.setServerId(1);
        mockDto.setEnvironmentId(1);
        mockDto.setExperienceId(1);
        mockDto.setListingTitle("Test template title");
        mockDto.setPlayStyleId(null);
        mockDto.setLegalityId(1);
        mockDto.setGroupStatusId(1);
        mockDto.setEventDate(null);
        mockDto.setEventTime(null);
        mockDto.setEventTimeZone(null);
        mockDto.setCategoryId(1);
        mockDto.setSubcategoryId(null);
        mockDto.setPvpStatusId(1);
        mockDto.setSystemId(1);
        mockDto.setPlanetId(null);
        mockDto.setListingDescription("Test template description");
        mockDto.setDesiredPartySize(2);
        mockDto.setCurrentPartySize(1);
        mockDto.setAvailableRoles(null);
        mockDto.setCommsOption("Optional");
        mockDto.setCommsService(null);
        mockDto.setLanguageCode(LanguageOptions.English);

        doAnswer(inv -> ResponseEntity.ok(Map.of("message", "Template saved: Test template title")))
                .when(lts).createTemplate(any(Users.class), any(CreateOrEditListingTemplateDto.class));

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createTemplate_Fail_ServiceError() throws Exception {


        CreateGroupListingDto mockDto = new CreateGroupListingDto();
        mockDto.setLanguageCode(LanguageOptions.English);

        doAnswer(inv -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Could note create the listing template.")))
                .when(lts).createTemplate(any(Users.class), any(CreateOrEditListingTemplateDto.class));

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createTemplate_Unauthorized_NoJwt() throws Exception {
        mockMvc.perform(post("/api/users/my/templates/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── deleteTemplate ────────────────────────────────────────────────────────

    @Test
    void deleteTemplate_Success() throws Exception {
        doAnswer(inv -> ResponseEntity.ok(Map.of("message", "Template deleted.")))
                .when(lts).removeTemplate(any(Users.class), anyLong());

        mockMvc.perform(delete("/api/users/my/templates/delete/5")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Template deleted."));
    }

    @Test
    void deleteTemplate_AlwaysReturns200_WhenNotFound() throws Exception {
        // Documents the "always 200" contract: JPQL WHERE user=:user AND id=:id returns 0 → "not found", not 404
        doAnswer(inv -> ResponseEntity.ok(Map.of("message", "Template not found")))
                .when(lts).removeTemplate(any(Users.class), anyLong());

        mockMvc.perform(delete("/api/users/my/templates/delete/99999")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "someKeycloakId"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Template not found"));
    }

    @Test
    void deleteTemplate_Unauthorized_NoJwt() throws Exception {
        mockMvc.perform(delete("/api/users/my/templates/delete/5"))
                .andExpect(status().isUnauthorized());
    }
}
