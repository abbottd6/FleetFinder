package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class ModListingActionRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private ModListingActionRepository mlar;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    /**
     * Inserts a mod_listing_action row with action_ts set via a SQL date expression.
     *
     * @param actionTsExpr SQL expression for the action_ts value, e.g.
     *                     "DATE_SUB(NOW(), INTERVAL 3 DAY)"
     */
    private void insertModAction(String actionTsExpr) {
        jdbcTemplate.update(
                "INSERT INTO mod_listing_action (id_user, username, action_type, action_ts) " +
                "VALUES (1, 'testuser', 'Auto', " + actionTsExpr + ")"
        );
    }

    @Test
    void findWeeksActions_ActionWithinSevenDays_IsReturned() {
        // given — action timestamped 3 days ago, inside the 7-day window
        insertModAction("DATE_SUB(NOW(), INTERVAL 3 DAY)");

        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ModListingAction> result = mlar.findWeeksActions(cutoff, pageable);

        // then
        assertFalse(result.isEmpty(),
                "An action timestamped 3 days ago should be returned by findWeeksActions with a 7-day cutoff");
    }

    @Test
    void findWeeksActions_ActionOlderThanSevenDays_IsExcluded() {
        // given — action timestamped 10 days ago, outside the 7-day window
        insertModAction("DATE_SUB(NOW(), INTERVAL 10 DAY)");

        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ModListingAction> result = mlar.findWeeksActions(cutoff, pageable);

        // then
        assertTrue(result.isEmpty(),
                "An action timestamped 10 days ago should be excluded by findWeeksActions with a 7-day cutoff");
    }

    @Test
    void findWeeksActions_MultipleActions_OnlyRecentOnesReturned() {
        // given — one recent action (3 days) and one old action (10 days)
        insertModAction("DATE_SUB(NOW(), INTERVAL 3 DAY)");
        insertModAction("DATE_SUB(NOW(), INTERVAL 10 DAY)");

        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ModListingAction> result = mlar.findWeeksActions(cutoff, pageable);

        // then
        assertAll("Only the recent action should be included:",
                () -> assertEquals(1, result.getTotalElements(),
                        "Exactly 1 action should be within the 7-day window"),
                () -> assertEquals("Auto", result.getContent().getFirst().getActionType(),
                        "The returned action should have actionType 'Auto'")
        );
    }

    @Test
    void findWeeksActions_NoActions_ReturnsEmptyPage() {
        // given — no mod_listing_action rows inserted for this transactional test
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ModListingAction> result = mlar.findWeeksActions(cutoff, pageable);

        // then
        assertTrue(result.isEmpty(),
                "findWeeksActions should return an empty page when there are no mod_listing_action records");
    }
}
