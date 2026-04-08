package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.ListingTemplateRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class ListingTemplateRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private ListingTemplateRepository ltr;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final String MOCK_KCID = "someKeycloakId";

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findByKeycloakId(MOCK_KCID)
                .orElseGet(() -> {
                    Users user = new Users();
                    user.setKeycloakId(MOCK_KCID);
                    user.setUsername("TestUser");
                    user.setEmail("test@gmail.com");
                    user.setIsDeleted(false);
                    return userRepository.save(user);
                });
    }

    /**
     * Inserts a listing_template row owned by the given user and returns the generated id_template.
     */
    private Long insertTemplate(Long userId) {
        jdbcTemplate.update(
                "INSERT INTO listing_template " +
                "(id_user, server_id, environment_id, experience_id, listing_title, " +
                "legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "listing_description, desired_party_size, current_party_size, comms_options, language_code) " +
                "VALUES (?, 1, 1, 1, 'Test Template Title', " +
                "1, 1, 1, 1, 1, " +
                "'Test template description.', 2, 1, 'Optional', 'English')",
                userId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_template) FROM listing_template", Long.class);
    }

    @Test
    void getListingTemplatesByUser_ReturnsOnlyOwnerTemplates() {
        // given: 2 templates for testUser, 1 for a different user
        Long testUserId = testUser.getUserId();
        insertTemplate(testUserId);
        insertTemplate(testUserId);

        Users otherUser = new Users();
        otherUser.setKeycloakId("otherKeycloakId");
        otherUser.setUsername("OtherUser");
        otherUser.setEmail("other@test.com");
        otherUser.setIsDeleted(false);
        otherUser.setInGameUsername("OtherUser");
        otherUser = userRepository.save(otherUser);
        insertTemplate(otherUser.getUserId());

        // when
        Page<?> result = ltr.getListingTemplatesByUser(testUser, PageRequest.of(0, 10));

        // then
        assertAll("only testUser's templates are returned:",
                () -> assertThat(result.getTotalElements()).isEqualTo(2),
                () -> result.getContent().forEach(t -> {
                    com.sc_fleetfinder.fleets.entities.ListingTemplate template =
                            (com.sc_fleetfinder.fleets.entities.ListingTemplate) t;
                    assertThat(template.getUser().getUserId()).isEqualTo(testUserId);
                })
        );
    }

    @Test
    void getListingTemplatesByUser_ReturnsEmptyPage_NoTemplates() {
        // given: no templates inserted

        // when
        Page<?> result = ltr.getListingTemplatesByUser(testUser, PageRequest.of(0, 10));

        // then
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void getListingTemplatesByUser_RespectsPageable() {
        // given: 5 templates for testUser
        Long testUserId = testUser.getUserId();
        for (int i = 0; i < 5; i++) {
            insertTemplate(testUserId);
        }

        // when: request page 0 with size 2
        Page<?> result = ltr.getListingTemplatesByUser(testUser, PageRequest.of(0, 2));

        // then
        assertAll("pagination is respected:",
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getTotalElements()).isEqualTo(5),
                () -> assertThat(result.getTotalPages()).isEqualTo(3)
        );
    }

    @Test
    void deleteByUserAndId_ReturnsOne_WhenMatchFound() {
        // given
        Long templateId = insertTemplate(testUser.getUserId());

        // when
        int deleted = ltr.deleteByUserAndId(testUser, templateId);

        // then
        assertThat(deleted).isEqualTo(1);
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM listing_template WHERE id_template = ?", Integer.class, templateId);
        assertThat(remaining).isEqualTo(0);
    }

    @Test
    void deleteByUserAndId_ReturnsZero_WhenIdNotFound() {
        // when: non-existent ID
        int deleted = ltr.deleteByUserAndId(testUser, 999999L);

        // then
        assertThat(deleted).isEqualTo(0);
    }

    @Test
    void deleteByUserAndId_ReturnsZero_WhenUserMismatch() {
        // given: template owned by a different user
        Users otherUser = new Users();
        otherUser.setKeycloakId("mismatchKeycloakId");
        otherUser.setUsername("MismatchUser");
        otherUser.setEmail("mismatch@test.com");
        otherUser.setIsDeleted(false);
        otherUser.setInGameUsername("MismatchUser");
        otherUser = userRepository.save(otherUser);
        Long templateId = insertTemplate(otherUser.getUserId());

        // when: attempt to delete as testUser (not the owner)
        int deleted = ltr.deleteByUserAndId(testUser, templateId);

        // then: JPQL WHERE user=:user AND id=:id matches no rows
        assertThat(deleted).isEqualTo(0);
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM listing_template WHERE id_template = ?", Integer.class, templateId);
        assertThat(remaining).isEqualTo(1);
    }
}
