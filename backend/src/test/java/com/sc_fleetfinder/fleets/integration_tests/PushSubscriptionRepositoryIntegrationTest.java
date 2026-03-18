package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
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
public class PushSubscriptionRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private PushSubscriptionRepository repo;

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
        testUser = userRepository.findByKeycloakId("pushSubTestKcId")
                .orElseGet(() -> {
                    Users u = new Users();
                    u.setKeycloakId("pushSubTestKcId");
                    u.setUsername("PushSubTestUser");
                    u.setEmail("pushsubtest@test.com");
                    u.setIsDeleted(false);
                    return userRepository.save(u);
                });

        otherUser = userRepository.findByKeycloakId("pushSubOtherKcId")
                .orElseGet(() -> {
                    Users u = new Users();
                    u.setKeycloakId("pushSubOtherKcId");
                    u.setUsername("PushSubOtherUser");
                    u.setEmail("pushsubother@test.com");
                    u.setIsDeleted(false);
                    return userRepository.save(u);
                });
    }

    /**
     * Inserts a push_subscription row for the given user and returns the generated id_push_sub.
     */
    private Long insertPushSub(Long userId, String deviceUrl) {
        jdbcTemplate.update(
                "INSERT INTO push_subscription (user_id, user_label, device_url, public_key, browser_secret, " +
                "sys_notes_enabled, group_notes_enabled, social_notes_enabled) " +
                "VALUES (?, 'Test Label', ?, 'pubKey123', 'secret123', 0, 1, 1)",
                userId, deviceUrl
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_push_sub) FROM push_subscription", Long.class);
    }

    // ─── getPushSubscriptionsByUser ───────────────────────────────────────────

    @Test
    void getPushSubscriptionsByUser_ReturnsOnlyOwnedSubs() {
        insertPushSub(testUser.getUserId(), "https://push.example.com/sub/1");
        insertPushSub(testUser.getUserId(), "https://push.example.com/sub/2");
        insertPushSub(otherUser.getUserId(), "https://push.example.com/sub/3");

        Page<PushSubscription> result = repo.getPushSubscriptionsByUser(testUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        result.getContent().forEach(sub ->
                assertThat(sub.getUser().getUserId()).isEqualTo(testUser.getUserId()));
    }

    @Test
    void getPushSubscriptionsByUser_EmptyPage_WhenNone() {
        // no subs inserted for testUser
        Page<PushSubscription> result = repo.getPushSubscriptionsByUser(testUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ─── findByUserAndDeviceUrl ───────────────────────────────────────────────

    @Test
    void findByUserAndDeviceUrl_Found() {
        String deviceUrl = "https://push.example.com/sub/abc";
        insertPushSub(testUser.getUserId(), deviceUrl);

        Optional<PushSubscription> result = repo.findByUserAndDeviceUrl(testUser, deviceUrl);

        assertThat(result).isPresent();
        assertThat(result.get().getDeviceUrl()).isEqualTo(deviceUrl);
    }

    @Test
    void findByUserAndDeviceUrl_WrongUser_NotFound() {
        String deviceUrl = "https://push.example.com/sub/abc";
        insertPushSub(otherUser.getUserId(), deviceUrl);

        // testUser does not own this device URL — should return empty
        Optional<PushSubscription> result = repo.findByUserAndDeviceUrl(testUser, deviceUrl);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserAndDeviceUrl_WrongUrl_NotFound() {
        insertPushSub(testUser.getUserId(), "https://push.example.com/sub/abc");

        Optional<PushSubscription> result = repo.findByUserAndDeviceUrl(testUser, "https://push.example.com/sub/different");

        assertThat(result).isEmpty();
    }

    // ─── findByUserAndIdPushSub ───────────────────────────────────────────────

    @Test
    void findByUserAndIdPushSub_Found() {
        Long subId = insertPushSub(testUser.getUserId(), "https://push.example.com/sub/find1");

        Optional<PushSubscription> result = repo.findByUserAndIdPushSub(testUser, subId);

        assertThat(result).isPresent();
        assertThat(result.get().getIdPushSub()).isEqualTo(subId);
    }

    @Test
    void findByUserAndIdPushSub_WrongUser_NotFound() {
        // Sub belongs to otherUser — testUser cannot find it by (testUser, id)
        Long subId = insertPushSub(otherUser.getUserId(), "https://push.example.com/sub/find2");

        Optional<PushSubscription> result = repo.findByUserAndIdPushSub(testUser, subId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserAndIdPushSub_WrongId_NotFound() {
        Optional<PushSubscription> result = repo.findByUserAndIdPushSub(testUser, 999999L);

        assertThat(result).isEmpty();
    }

    // ─── deleteByUserAndIdPushSub ─────────────────────────────────────────────

    @Test
    void deleteByUserAndIdPushSub_Success_ReturnsOne() {
        Long subId = insertPushSub(testUser.getUserId(), "https://push.example.com/sub/del1");

        Integer deleted = repo.deleteByUserAndIdPushSub(testUser, subId);

        assertThat(deleted).isEqualTo(1);
        // Flush to force JPA to execute the pending DELETE SQL before JDBC verification.
        // Spring Data JPA's derived deleteBy methods defer the SQL until the persistence context flushes.
        repo.flush();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM push_subscription WHERE id_push_sub = ?", Integer.class, subId);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteByUserAndIdPushSub_WrongUser_ReturnsZero() {
        // Sub belongs to otherUser — testUser's delete should affect 0 rows
        Long subId = insertPushSub(otherUser.getUserId(), "https://push.example.com/sub/del2");

        Integer deleted = repo.deleteByUserAndIdPushSub(testUser, subId);

        assertThat(deleted).isEqualTo(0);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM push_subscription WHERE id_push_sub = ?", Integer.class, subId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void deleteByUserAndIdPushSub_NotFound_ReturnsZero() {
        Integer deleted = repo.deleteByUserAndIdPushSub(testUser, 999999L);

        assertThat(deleted).isEqualTo(0);
    }
}
