package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ModClearIssueDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.ModerationController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ModerationController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModerationService mods;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserActivityCache userActivityCache;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String MOD_KEYCLOAK_ID = "someModKeycloakId";

    private Users buildModUser() {
        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");
        mod.setKeycloakId(MOD_KEYCLOAK_ID);
        mod.setEmail("mod@example.com");
        return mod;
    }

    // ─── POST /api/modctrl/mod_delete_listing ────────────────────────────────────

    @Test
    void modDeleteListing_Success_Returns200WithListingTitle() throws Exception {
        // given
        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(1L);
        dto.setReportBasis(1);
        dto.setNote("Policy violation");

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID)).thenReturn(Optional.of(buildModUser()));
        doAnswer(inv -> ResponseEntity.status(HttpStatus.OK).body(Map.of("listingTitle", "Test Listing")))
                .when(mods).modDeleteListing(any(ManualModDeleteDto.class), any(Users.class));

        // when / then
        mockMvc.perform(post("/api/modctrl/mod_delete_listing")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listingTitle").value("Test Listing"));
    }

    @Test
    void modDeleteListing_ModUserNotFound_Returns404() throws Exception {
        // given
        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(1L);
        dto.setReportBasis(1);

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID))
                .thenReturn(Optional.empty());

        // when / then
        mockMvc.perform(post("/api/modctrl/mod_delete_listing")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void modDeleteListing_WithoutModRole_Returns403() throws Exception {
        // given
        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(1L);
        dto.setReportBasis(1);

        // when / then — ROLE_user does not satisfy hasRole('mod')
        mockMvc.perform(post("/api/modctrl/mod_delete_listing")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ─── PUT /api/modctrl/mod_clear_issue ────────────────────────────────────────

    @Test
    void modClearIssue_Success_Returns200() throws Exception {
        // given
        ModClearIssueDto dto = new ModClearIssueDto();
        dto.setIssueId(1L);
        dto.setNote("Cleared on review");

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID)).thenReturn(Optional.of(buildModUser()));
        doAnswer(inv -> ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Report counters reset for issue on groupId: 1")))
                .when(mods).modClearIssue(any(ModClearIssueDto.class), any(Users.class));

        // when / then
        mockMvc.perform(put("/api/modctrl/mod_clear_issue")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void modClearIssue_ModUserNotFound_Returns404() throws Exception {
        // given
        ModClearIssueDto dto = new ModClearIssueDto();
        dto.setIssueId(1L);

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID)).thenReturn(Optional.empty());

        // when / then
        mockMvc.perform(put("/api/modctrl/mod_clear_issue")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void modClearIssue_WithoutModRole_Returns403() throws Exception {
        // given
        ModClearIssueDto dto = new ModClearIssueDto();
        dto.setIssueId(1L);

        // when / then
        mockMvc.perform(put("/api/modctrl/mod_clear_issue")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ─── GET /api/modctrl ────────────────────────────────────────────────────────

    @Test
    void modGetAllGroupListings_Success_Returns200WithPage() throws Exception {
        // given
        GroupListingResponseDto listingDto = new GroupListingResponseDto();
        listingDto.setGroupId(1L);
        listingDto.setListingTitle("Test Listing");
        Page<GroupListingResponseDto> page = new PageImpl<>(List.of(listingDto));

        when(mods.modGetAllGroupListings(any(Pageable.class))).thenReturn(page);

        // when / then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/modctrl")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].groupId").value(1L));
    }

    @Test
    void modGetAllGroupListings_WithoutModRole_Returns403() throws Exception {
        // when / then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/modctrl")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    // ─── POST /api/modctrl/get_issues_page ───────────────────────────────────────

    @Test
    void modGetAllIssues_Success_Returns200WithPage() throws Exception {
        // given
        SortablePageRequestDto pageDto = new SortablePageRequestDto();
        pageDto.setPage(0);
        pageDto.setSize(10);

        ModerationIssueResponseDto issueDto = new ModerationIssueResponseDto();
        issueDto.setIssueId(1L);
        issueDto.setGroupId(1L);
        issueDto.setUserId(1L);
        issueDto.setUsername("TestUser");
        issueDto.setReportTotalCount(5);
        issueDto.setSpamCount(5);
        issueDto.setHateSpeechCount(0);
        issueDto.setNsfwCount(0);
        issueDto.setScamCount(0);
        issueDto.setOffTopicCount(0);
        issueDto.setTrollCount(0);
        issueDto.setDoxxCount(0);
        issueDto.setCheatCount(0);
        issueDto.setOtherCount(0);
        issueDto.setStatus("Pending");
        issueDto.setFirstReportTs(Instant.now());
        issueDto.setLastReportTs(Instant.now());

        Page<ModerationIssueResponseDto> page = new PageImpl<>(List.of(issueDto));
        when(mods.modGetAllIssues(any(Pageable.class))).thenReturn(page);

        // when / then
        mockMvc.perform(post("/api/modctrl/get_issues_page")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].issueId").value(1L));
    }

    @Test
    void modGetAllIssues_WithoutModRole_Returns403() throws Exception {
        // given
        SortablePageRequestDto pageDto = new SortablePageRequestDto();
        pageDto.setPage(0);
        pageDto.setSize(10);

        // when / then
        mockMvc.perform(post("/api/modctrl/get_issues_page")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isForbidden());
    }

    // ─── POST /api/modctrl/weeks_actions ─────────────────────────────────────────

    @Test
    void getThisWeeksModActions_Success_Returns200() throws Exception {
        // given
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        ModListingActionDto actionDto = new ModListingActionDto();
        actionDto.setActionId(1L);
        actionDto.setUserId(1L);
        actionDto.setUsername("TestUser");
        actionDto.setActionType("Auto");
        actionDto.setActionTs(Instant.now());

        Page<ModListingActionDto> page = new PageImpl<>(List.of(actionDto));

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID)).thenReturn(Optional.of(buildModUser()));
        when(mods.getThisWeeksModListingActions(any(Pageable.class))).thenReturn(page);

        // when / then
        mockMvc.perform(post("/api/modctrl/weeks_actions")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].actionId").value(1L));
    }

    @Test
    void getThisWeeksModActions_ModUserNotFound_Returns404() throws Exception {
        // given
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        when(userRepository.findByKeycloakId(MOD_KEYCLOAK_ID)).thenReturn(Optional.empty());

        // when / then
        mockMvc.perform(post("/api/modctrl/weeks_actions")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOD_KEYCLOAK_ID))
                                .authorities(new SimpleGrantedAuthority("ROLE_mod")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isNotFound());
    }
}
