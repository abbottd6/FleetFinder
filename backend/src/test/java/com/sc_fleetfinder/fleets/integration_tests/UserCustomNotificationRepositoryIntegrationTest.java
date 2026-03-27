package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.UserCustomNotificationRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class UserCustomNotificationRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private UserCustomNotificationRepository cnr;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private Users testUser;
    private Users otherUser;

    @BeforeEach
    void insertUsers() {
        testUser = userRepository.findByKeycloakId("customNoteTestKcId")
                .orElseGet(() -> {
                    Users u = new Users();
                    u.setKeycloakId("customNoteTestKcId");
                    u.setUsername("CustomNoteTestUser");
                    u.setEmail("customnotetest@test.com");
                    u.setIsDeleted(false);
                    return userRepository.save(u);
                });

        otherUser = userRepository.findByKeycloakId("customNoteOtherKcId")
                .orElseGet(() -> {
                    Users u = new Users();
                    u.setKeycloakId("customNoteOtherKcId");
                    u.setUsername("CustomNoteOtherUser");
                    u.setEmail("customnoteother@test.com");
                    u.setIsDeleted(false);
                    return userRepository.save(u);
                });
    }

    /**
     * Inserts a minimal user_custom_notification row (all FK ref fields are nullable).
     * Returns the generated id_custom_note.
     */
    private Long insertCustomNote(Long userId) {
        jdbcTemplate.update(
                "INSERT INTO user_custom_notification (user_id, enabled) VALUES (?, 1)",
                userId
        );
        return jdbcTemplate.queryForObject(
                "SELECT MAX(id_custom_note) FROM user_custom_notification", Long.class);
    }

    // ─── findByUser ───────────────────────────────────────────────────────────

    @Test
    void findByUser_ReturnsOnlyUserOwnedRows() {
        insertCustomNote(testUser.getUserId());
        insertCustomNote(testUser.getUserId());
        insertCustomNote(otherUser.getUserId());

        Page<UserCustomNotification> result = cnr.findByUser(testUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        result.getContent().forEach(note ->
                assertThat(note.getUser().getUserId()).isEqualTo(testUser.getUserId()));
    }

    @Test
    void findByUser_EmptyResult_WhenUserHasNone() {
        // No rows inserted for testUser
        Page<UserCustomNotification> result = cnr.findByUser(testUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByUser_PaginationRespected() {
        insertCustomNote(testUser.getUserId());
        insertCustomNote(testUser.getUserId());
        insertCustomNote(testUser.getUserId());

        Page<UserCustomNotification> result = cnr.findByUser(testUser, PageRequest.of(0, 2));

        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    // ─── countByUser ──────────────────────────────────────────────────────────

    @Test
    void countByUser_ReturnsCorrectCount() {
        insertCustomNote(testUser.getUserId());
        insertCustomNote(testUser.getUserId());
        insertCustomNote(testUser.getUserId());

        Integer count = cnr.countByUser(testUser);

        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByUser_ReturnsZeroForNewUser() {
        Integer count = cnr.countByUser(testUser);

        assertThat(count).isEqualTo(0);
    }

    // ─── findByUserAndCustomNoteId ─────────────────────────────────────────────

    @Test
    void findByUserAndCustomNoteId_Found_ReturnsEntity() {
        Long noteId = insertCustomNote(testUser.getUserId());

        Optional<UserCustomNotification> result = cnr.findByUserAndCustomNoteId(testUser, noteId);

        assertThat(result).isPresent();
        assertThat(result.get().getCustomNoteId()).isEqualTo(noteId);
    }

    @Test
    void findByUserAndCustomNoteId_WrongUser_ReturnsEmpty() {
        Long noteId = insertCustomNote(otherUser.getUserId());

        Optional<UserCustomNotification> result = cnr.findByUserAndCustomNoteId(testUser, noteId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserAndCustomNoteId_NonExistentId_ReturnsEmpty() {
        Optional<UserCustomNotification> result = cnr.findByUserAndCustomNoteId(testUser, 999999L);

        assertThat(result).isEmpty();
    }

    // ─── deleteByUserIdAndNoteId ───────────────────────────────────────────────

    @Test
    void deleteByUserIdAndNoteId_DeletesOwnRow_ReturnsOne() {
        Long noteId = insertCustomNote(testUser.getUserId());

        int deleted = cnr.deleteByUserIdAndNoteId(testUser.getUserId(), noteId);

        assertThat(deleted).isEqualTo(1);
        cnr.flush();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_custom_notification WHERE id_custom_note = ?",
                Integer.class, noteId);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteByUserIdAndNoteId_WrongUser_ReturnsZero() {
        Long noteId = insertCustomNote(otherUser.getUserId());

        int deleted = cnr.deleteByUserIdAndNoteId(testUser.getUserId(), noteId);

        assertThat(deleted).isEqualTo(0);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_custom_notification WHERE id_custom_note = ?",
                Integer.class, noteId);
        assertThat(count).isEqualTo(1);
    }
}
