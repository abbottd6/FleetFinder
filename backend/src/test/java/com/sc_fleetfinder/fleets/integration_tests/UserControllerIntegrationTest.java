package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class UserControllerIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final String MOCK_KCID = "someKeycloakId";
    private static final String MOCK_USERNAME = "TestUser";
    private static final String MOCK_EMAIL = "test@gmail.com";

    @BeforeEach
    void verifyTestUser() {
        userRepository.findByKeycloakId(MOCK_KCID)
                .orElseGet(() -> {
                    Users mockUser = new Users();
                    mockUser.setKeycloakId(MOCK_KCID);
                    mockUser.setEmail(MOCK_EMAIL);
                    mockUser.setUsername(MOCK_USERNAME);
                    mockUser.setIsDeleted(false);

                    return userRepository.save(mockUser);
                });
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Long getTestUserId() {
        return userRepository.findByKeycloakId(MOCK_KCID).orElseThrow().getUserId();
    }

    private Long insertTemplate(Long userId) {
        jdbcTemplate.update(
                "INSERT INTO listing_template " +
                "(id_user, server_id, environment_id, experience_id, listing_title, " +
                "legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "listing_description, desired_party_size, current_party_size, comms_options, language_code) " +
                "VALUES (?, 1, 1, 1, 'Integration Test Template', " +
                "1, 1, 1, 1, 1, " +
                "'Integration test template description.', 2, 1, 'Optional', 'English')",
                userId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_template) FROM listing_template", Long.class);
    }

    // ─── user endpoint tests (existing) ───────────────────────────────────────

    @Test
    void testGetUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").isNumber())
                .andExpect(jsonPath("$[0].username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$[0].server").exists())
                .andExpect(jsonPath("$[0].org").exists())
                .andExpect(jsonPath("$[0].about").exists())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].groupListingsDto").doesNotExist())
                .andExpect(jsonPath("$[0].acctCreated").doesNotExist());
    }

    @Test
    void testGetUsers_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$.server").exists())
                .andExpect(jsonPath("$.org").exists())
                .andExpect(jsonPath("$.about").exists())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.groupListingsDto").doesNotExist())
                .andExpect(jsonPath("$.acctCreated").doesNotExist());
    }

    @Test
    void testGetUserById_Failure() throws Exception {
        mockMvc.perform(get("/api/users/350")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUsersById_Failure_NoAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMe_Found() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$.server").exists())
                .andExpect(jsonPath("$.org").exists())
                .andExpect(jsonPath("$.about").exists())
                .andExpect(jsonPath("$.acctCreated").exists())
                .andExpect(jsonPath("$.groupListingsDto").exists());
    }

    @Test
    void testGetMe_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .with(jwt().jwt(j -> j.subject("doesNotExist")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMe_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser_SuccessFromMe404() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(jwt().jwt(j -> j
                        .subject("newUserKeycloakId")
                        .claim("preferred_username", "newUser")
                        .claim("email", "newuser@gmail.com")
                        .claim("discord_user_id", "20characterdiscordid")
                        .claim("discord_username", "mockDiscordUsername")
                )
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.discordUsername").value("mockDiscordUsername"))
                .andExpect(jsonPath("$.externalSysNotesEnabled").value(false))
                .andExpect(jsonPath("$.externalGroupNotesEnabled").value(false))
                .andExpect(jsonPath("$.externalSocialNotesEnabled").value(false));
    }

    @Test
    void testCreateUser_SuccessFromMe404_DiscordNull() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                        .with(jwt().jwt(j -> j
                                        .subject("anotherUserKeycloakId")
                                        .claim("preferred_username", "newestUser")
                                        .claim("email", "newestuser@gmail.com")
                                        .claim("discord_user_id", null)
                                        .claim("discord_username", null)
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value("newestUser"))
                .andExpect(jsonPath("$.discordUsername").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.externalSysNotesEnabled").value(false))
                .andExpect(jsonPath("$.externalGroupNotesEnabled").value(false))
                .andExpect(jsonPath("$.externalSocialNotesEnabled").value(false));
    }

    @Test
    void testCreateUser_FailExistingKeycloak() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateUser_FailNoAuth() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    // ─── template endpoint tests ───────────────────────────────────────────────

    @Test
    void testGetTemplates_Success_ReturnsTwoTemplates() throws Exception {
        Long userId = getTestUserId();
        insertTemplate(userId);
        insertTemplate(userId);

        mockMvc.perform(post("/api/users/my/templates/get")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":0,\"size\":10,\"sortField\":null,\"sortDirection\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].languageCode").value("English"));
    }

    @Test
    void testGetTemplates_Success_EmptyPageWhenNoTemplates() throws Exception {
        // no templates inserted

        mockMvc.perform(post("/api/users/my/templates/get")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":0,\"size\":10,\"sortField\":null,\"sortDirection\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetTemplates_Unauthorized() throws Exception {
        // CSRF token needed because SecurityConfig is @Profile("!test") — default CSRF is enabled in test profile
        mockMvc.perform(post("/api/users/my/templates/get")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":0,\"size\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTemplate_Success_AllFields() throws Exception {
        CreateGroupListingDto dto = new CreateGroupListingDto();
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Integration template title");
        dto.setPlayStyleId(1);
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setEventDate("2025-06-15");
        dto.setEventTime("18:30:00");
        dto.setEventTimeZone("UTC");
        dto.setCategoryId(1);
        dto.setSubcategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setPlanetId(1);
        dto.setListingDescription("Integration template description");
        dto.setDesiredPartySize(3);
        dto.setCurrentPartySize(1);
        dto.setAvailableRoles("Integration roles");
        dto.setCommsOption("Optional");
        dto.setCommsService("Discord");
        dto.setLanguageCode(LanguageOptions.English);

        Long userId = getTestUserId();

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", startsWith("Template saved:")));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM listing_template WHERE id_user = ?", Integer.class, userId);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCreateTemplate_Success_RequiredFieldsOnly() throws Exception {
        // No @Valid on this endpoint — nullable fields accepted as null
        CreateGroupListingDto dto = new CreateGroupListingDto();
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Required fields only title");
        dto.setPlayStyleId(null);
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setEventDate(null);
        dto.setEventTime(null);
        dto.setEventTimeZone(null);
        dto.setCategoryId(1);
        dto.setSubcategoryId(null);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setPlanetId(null);
        dto.setListingDescription("Required fields only description");
        dto.setDesiredPartySize(2);
        dto.setCurrentPartySize(1);
        dto.setAvailableRoles(null);
        dto.setCommsOption("Optional");
        dto.setCommsService(null);
        dto.setLanguageCode(LanguageOptions.English);

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", startsWith("Template saved:")));
    }

    @Test
    void testCreateTemplate_Fail_InvalidServerId() throws Exception {
        // serverId=9999 has no FK match → conversion throws → service catch → 500
        CreateGroupListingDto dto = new CreateGroupListingDto();
        dto.setServerId(9999);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle("Invalid server template");
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setCategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setListingDescription("Description");
        dto.setDesiredPartySize(2);
        dto.setCurrentPartySize(1);
        dto.setCommsOption("Optional");
        dto.setLanguageCode(LanguageOptions.English);

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateTemplate_Unauthorized() throws Exception {
        // CSRF token needed because SecurityConfig is @Profile("!test") — default CSRF is enabled in test profile
        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteTemplate_Success() throws Exception {
        Long userId = getTestUserId();
        Long templateId = insertTemplate(userId);

        mockMvc.perform(delete("/api/users/my/templates/delete/" + templateId)
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Template deleted."));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM listing_template WHERE id_template = ?", Integer.class, templateId);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testDeleteTemplate_Returns200_WhenNotFound() throws Exception {
        // Non-existent ID → JPQL WHERE returns 0 → "Template not found", NOT 404
        mockMvc.perform(delete("/api/users/my/templates/delete/99999")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Template not found"));
    }

    @Test
    void testDeleteTemplate_Returns200_WhenOwnedByDifferentUser() throws Exception {
        // Template belongs to user2 — authenticating as MOCK_KCID should return "not found" (not 401)
        // because the JPQL WHERE user=:user AND id=:id simply matches 0 rows
        Users user2 = new Users();
        user2.setKeycloakId("differentOwnerKcId");
        user2.setUsername("DifferentOwner");
        user2.setEmail("differentowner@test.com");
        user2.setIsDeleted(false);
        user2 = userRepository.save(user2);
        Long templateId = insertTemplate(user2.getUserId());

        mockMvc.perform(delete("/api/users/my/templates/delete/" + templateId)
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Template not found"));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM listing_template WHERE id_template = ?", Integer.class, templateId);
        assertThat(count).isEqualTo(1);
    }

    // ─── timezone conversion tests ─────────────────────────────────────────────

    /**
     * Builds a minimal valid CreateGroupListingDto for use in timezone conversion tests.
     * Event schedule fields are left unset so callers can supply their own.
     */
    private CreateGroupListingDto buildBaseTemplateDto(String title) {
        CreateGroupListingDto dto = new CreateGroupListingDto();
        dto.setServerId(1);
        dto.setEnvironmentId(1);
        dto.setExperienceId(1);
        dto.setListingTitle(title);
        dto.setLegalityId(1);
        dto.setGroupStatusId(1);
        dto.setCategoryId(1);
        dto.setPvpStatusId(1);
        dto.setSystemId(1);
        dto.setListingDescription("Timezone conversion test description");
        dto.setDesiredPartySize(2);
        dto.setCurrentPartySize(1);
        dto.setCommsOption("Optional");
        dto.setLanguageCode(LanguageOptions.English);
        return dto;
    }

    @Test
    void testCreateTemplate_EventSchedule_ConvertsToUtc_PacificDaylightTime() throws Exception {
        // America/Los_Angeles is UTC-7 during summer (PDT)
        // 2025-06-15 18:30:00 PDT  →  2025-06-16 01:30:00 UTC
        CreateGroupListingDto dto = buildBaseTemplateDto("Timezone test — Los Angeles");
        dto.setEventDate("2025-06-15");
        dto.setEventTime("18:30:00");
        dto.setEventTimeZone("America/Los_Angeles");

        Long userId = getTestUserId();

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Expected: same ZonedDateTime calculation the mapper performs → 2025-06-16T01:30:00Z
        // (PDT = UTC-7, so 18:30 local + 7h = 01:30 UTC next day)
        Instant expected = ZonedDateTime.of(
                LocalDate.parse("2025-06-15"),
                LocalTime.parse("18:30:00"),
                ZoneId.of("America/Los_Angeles")
        ).toInstant();

        // UNIX_TIMESTAMP() always returns UTC epoch seconds regardless of JDBC/JVM timezone.
        // This avoids any Timestamp timezone misinterpretation at the JDBC layer.
        Long actualEpochSeconds = jdbcTemplate.queryForObject(
                "SELECT UNIX_TIMESTAMP(event_schedule) FROM listing_template " +
                "WHERE id_user = ? ORDER BY id_template DESC LIMIT 1",
                Long.class, userId);

        assertThat(actualEpochSeconds).isEqualTo(expected.getEpochSecond());
    }

    @Test
    void testCreateTemplate_EventSchedule_ConvertsToUtc_EasternStandardTime() throws Exception {
        // America/New_York is UTC-5 during winter (EST)
        // 2025-01-15 09:00:00 EST  →  2025-01-15 14:00:00 UTC
        CreateGroupListingDto dto = buildBaseTemplateDto("Timezone test — New York");
        dto.setEventDate("2025-01-15");
        dto.setEventTime("09:00:00");
        dto.setEventTimeZone("America/New_York");

        Long userId = getTestUserId();

        mockMvc.perform(post("/api/users/my/templates/save")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Expected: same ZonedDateTime calculation the mapper performs → 2025-01-15T14:00:00Z
        // (EST = UTC-5, so 09:00 local + 5h = 14:00 UTC same day)
        Instant expected = ZonedDateTime.of(
                LocalDate.parse("2025-01-15"),
                LocalTime.parse("09:00:00"),
                ZoneId.of("America/New_York")
        ).toInstant();

        // UNIX_TIMESTAMP() always returns UTC epoch seconds regardless of JDBC/JVM timezone.
        Long actualEpochSeconds = jdbcTemplate.queryForObject(
                "SELECT UNIX_TIMESTAMP(event_schedule) FROM listing_template " +
                "WHERE id_user = ? ORDER BY id_template DESC LIMIT 1",
                Long.class, userId);

        assertThat(actualEpochSeconds).isEqualTo(expected.getEpochSecond());
    }
}
