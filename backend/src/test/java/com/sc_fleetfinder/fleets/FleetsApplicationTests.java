package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.integration_tests.AbstractIntegrationTestDB;
import com.sc_fleetfinder.fleets.scheduledTasks.CleanupNewListingQueueTask;
import com.sc_fleetfinder.fleets.scheduledTasks.ExpiredListingDeleteTask;
import com.sc_fleetfinder.fleets.scheduledTasks.ListingVisStatusMaintenanceTask;
import com.sc_fleetfinder.fleets.scheduledTasks.NewListingNotificationQueueTask;
import com.sc_fleetfinder.fleets.scheduledTasks.OutboxCleanupTask;
import com.sc_fleetfinder.fleets.scheduledTasks.OutboxSenderTask;
import com.sc_fleetfinder.fleets.scheduledTasks.OutboxTaskService;
import com.sc_fleetfinder.fleets.scheduledTasks.UserActivityCacheClean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
@ActiveProfiles("test")
class FleetsApplicationTests extends AbstractIntegrationTestDB {

    @Autowired(required = false)
    TaskScheduler taskScheduler;

    @MockitoBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockitoBean
    CleanupNewListingQueueTask cleanupNewListingQueueTask;

    @MockitoBean
    ExpiredListingDeleteTask expiredListingDeleteTask;

    @MockitoBean
    ListingVisStatusMaintenanceTask listingVisStatusMaintenanceTask;

    @MockitoBean
    NewListingNotificationQueueTask newListingNotificationQueueTask;

    @MockitoBean
    OutboxCleanupTask outboxCleanupTask;

    @MockitoBean
    OutboxSenderTask outboxSenderTask;

    @MockitoBean
    UserActivityCacheClean userActivityCacheClean;

    @Test
    void schedulingIsDisabled() {
        assertNull(taskScheduler);
    }
}
