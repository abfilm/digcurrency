package com.digcurrency.rates;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * Settings for the live rate sources, bound from {@code digcurrency.rates.*}.
 *
 * @param enabled         whether rates are refreshed automatically
 * @param refreshInterval time between two refreshes
 * @param coingecko       CoinGecko settings (crypto rates)
 * @param ecb             European Central Bank settings (fiat rates)
 * @param pegs            currencies that take their rate from another one, e.g. {@code EDEUR -> EUR}
 */
@ConfigurationProperties("digcurrency.rates")
public record RateProperties(
        boolean enabled,
        Duration refreshInterval,
        CoinGecko coingecko,
        Ecb ecb,
        Map<String, String> pegs) {

    /**
     * @param baseUrl CoinGecko API base URL
     * @param apiKey  optional demo API key, sent as {@code x-cg-demo-api-key}
     * @param ids     currency code to CoinGecko coin id, e.g. {@code BTC -> bitcoin}
     */
    public record CoinGecko(String baseUrl, String apiKey, Map<String, String> ids) {
    }

    /** @param url ECB daily euro reference rates (XML) */
    public record Ecb(String url) {
    }
}
