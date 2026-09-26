package com.digcurrency.currency;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Loads demo data for the showcase. Disabled in the "test" profile. */
@Component
@Profile("!test")
class CurrencySeeder implements CommandLineRunner {

    private final CurrencyRepository repository;

    CurrencySeeder(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Currency("USD", "US Dollar", CurrencyType.FIAT, new BigDecimal("1")),
                new Currency("EUR", "Euro", CurrencyType.FIAT, new BigDecimal("1.08")),
                new Currency("GBP", "British Pound", CurrencyType.FIAT, new BigDecimal("1.27")),
                new Currency("BTC", "Bitcoin", CurrencyType.CRYPTO, new BigDecimal("65000")),
                new Currency("ETH", "Ether", CurrencyType.CRYPTO, new BigDecimal("3200")),
                new Currency("EDEUR", "Digital Euro (demo)", CurrencyType.CBDC, new BigDecimal("1.08"))));
    }
}
