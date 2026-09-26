package com.digcurrency.rates;

import com.digcurrency.currency.Currency;
import com.digcurrency.currency.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pulls rates from every {@link RateProvider} and writes them to the stored currencies.
 * A source that fails is skipped, so its currencies keep their last known rate.
 * Currencies no source knows about (and that aren't pegged) are left unchanged.
 */
@Service
@Transactional(readOnly = true)
public class RateRefreshService {

    private static final Logger log = LoggerFactory.getLogger(RateRefreshService.class);

    private final List<RateProvider> providers;
    private final CurrencyRepository repository;
    private final Map<String, String> pegs;

    RateRefreshService(List<RateProvider> providers, CurrencyRepository repository, RateProperties properties) {
        this.providers = providers;
        this.repository = repository;
        this.pegs = new HashMap<>();
        if (properties.pegs() != null) {
            properties.pegs().forEach((code, target) ->
                    pegs.put(code.toUpperCase(Locale.ROOT), target.toUpperCase(Locale.ROOT)));
        }
    }

    @Transactional
    public RateRefreshResult refresh() {
        Map<String, BigDecimal> rates = new HashMap<>();
        List<String> failed = new ArrayList<>();
        for (RateProvider provider : providers) {
            try {
                rates.putAll(provider.fetchUsdRates());
            } catch (RuntimeException e) {
                log.warn("Rate source {} failed, keeping previous rates: {}", provider.name(), e.getMessage());
                failed.add(provider.name());
            }
        }

        List<String> updated = new ArrayList<>();
        for (Currency currency : repository.findAllByOrderByCodeAsc()) {
            String source = pegs.getOrDefault(currency.getCode(), currency.getCode());
            BigDecimal rate = rates.get(source);
            if (rate != null) {
                currency.updateRate(rate);
                updated.add(currency.getCode());
            }
        }

        log.info("Refreshed rates for {}{}", updated, failed.isEmpty() ? "" : " (failed sources: " + failed + ")");
        return new RateRefreshResult(updated, failed);
    }
}
