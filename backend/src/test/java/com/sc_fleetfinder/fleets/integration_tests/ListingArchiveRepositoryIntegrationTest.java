package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class ListingArchiveRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private ListingArchiveRepository archiveRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // Inserts a group_listing that satisfies the WHERE clause in generateArchivesForScheduledRemoval:
    //   vis_status = 'ARCHIVED', last_updated > 14 days ago, event_schedule IS NULL
    private Long insertArchivedListing(String languageCode) {
        jdbcTemplate.update(
                "INSERT INTO group_listing " +
                "(id_user, server_id, environment_id, experience_id, listing_title, " +
                "legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Archive repo test listing', " +
                "1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', ?, " +
                "'ARCHIVED', DATE_SUB(NOW(), INTERVAL 30 DAY), 'Test description for archive.')",
                languageCode
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);
    }

    @Test
    void generateArchivesForScheduledRemoval_CopiesLanguageCode_ToListingArchive() {
        // given: an archived listing with language_code = French
        Long groupId = insertArchivedListing("French");

        // when
        int count = archiveRepo.generateArchivesForScheduledRemoval();

        // then
        Optional<ListingArchive> result = archiveRepo.findByGroupId(groupId);
        assertAll("language_code is copied to listing_archive:",
                () -> assertTrue(count >= 1, "Expected at least 1 row inserted into listing_archive"),
                () -> assertTrue(result.isPresent(), "Expected a listing_archive row for groupId " + groupId),
                () -> assertEquals(LanguageOptions.French, result.get().getLanguageCode(),
                        "language_code should be copied from group_listing to listing_archive")
        );
    }

    @Test
    void generateArchivesForScheduledRemoval_PreservesDistinctLanguageCodes() {
        // given: two archived listings with different language codes
        Long frenchGroupId = insertArchivedListing("French");
        Long germanGroupId = insertArchivedListing("German");

        // when
        archiveRepo.generateArchivesForScheduledRemoval();

        // then
        ListingArchive frenchArchive = archiveRepo.findByGroupId(frenchGroupId).orElseThrow();
        ListingArchive germanArchive = archiveRepo.findByGroupId(germanGroupId).orElseThrow();
        assertAll("distinct language codes are independently preserved in archive:",
                () -> assertEquals(LanguageOptions.French, frenchArchive.getLanguageCode()),
                () -> assertEquals(LanguageOptions.German, germanArchive.getLanguageCode())
        );
    }

    @Test
    void generateArchivesForScheduledRemoval_DoesNotArchive_WhenVisStatusIsNotARCHIVED() {
        // given: a listing with vis_status = 'FRESH' (should be excluded by WHERE clause)
        jdbcTemplate.update(
                "INSERT INTO group_listing " +
                "(id_user, server_id, environment_id, experience_id, listing_title, " +
                "legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Non-archived listing', " +
                "1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', " +
                "'FRESH', DATE_SUB(NOW(), INTERVAL 30 DAY), 'Test description.')"
        );
        Long groupId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        // when
        archiveRepo.generateArchivesForScheduledRemoval();

        // then
        Optional<ListingArchive> result = archiveRepo.findByGroupId(groupId);
        assertFalse(result.isPresent(), "A listing with vis_status=FRESH should not be copied to listing_archive");
    }

    @Test
    void generateArchivesForScheduledRemoval_DoesNotArchive_WhenLastUpdatedIsRecent() {
        // given: an ARCHIVED listing where last_updated is only 1 day ago (does not meet the 14-day threshold)
        jdbcTemplate.update(
                "INSERT INTO group_listing " +
                "(id_user, server_id, environment_id, experience_id, listing_title, " +
                "legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Recently updated listing', " +
                "1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', " +
                "'ARCHIVED', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Test description.')"
        );
        Long groupId = jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);

        // when
        archiveRepo.generateArchivesForScheduledRemoval();

        // then
        Optional<ListingArchive> result = archiveRepo.findByGroupId(groupId);
        assertFalse(result.isPresent(), "A listing updated within the last 14 days should not be archived");
    }
}
