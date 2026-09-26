package com.digcurrency.rates;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refreshes rates once the app is ready (after the demo data is seeded) and then every
 * {@code digcurrency.rates.refresh-interval}. Off in the "test" profile and when
 * {@code digcurrency.rates.enabled=false}.
 */
@Component
@Profile("!test")
@ConditionalOnProperty(prefix = "digcurrency.rates", name = "enabled", havingValue = "true", matchIfMissing = true)
class RateRefreshScheduler {

    private final RateRefreshService service;

    RateRefreshScheduler(RateRefreshService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    void refreshOnStartup() {
        service.refresh();
    }

    @Scheduled(fixedDelayString = "${digcurrency.rates.refresh-interval}",
            initialDelayString = "${digcurrency.rates.refresh-interval}")
    void refreshPeriodically() {
        service.refresh();
    }
}
