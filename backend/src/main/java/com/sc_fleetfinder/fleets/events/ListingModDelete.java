package com.sc_fleetfinder.fleets.events;

import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class ListingModDelete {

    private final ModerationService modService;

    public ListingModDelete(ModerationService modService) {
        this.modService = modService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReportThresholdExcess(ListingModDeleteEvent event) {
        try {
            modService.autoModDeleteListing(event.listing(), event.owner());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
