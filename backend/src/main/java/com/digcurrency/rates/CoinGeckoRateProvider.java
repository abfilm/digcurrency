package com.digcurrency.rates;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Crypto rates in USD from the CoinGecko {@code /simple/price} endpoint. */
@Component
class CoinGeckoRateProvider implements RateProvider {

    private final RestClient restClient;
    private final Map<String, String> idsByCode;

    CoinGeckoRateProvider(RestClient.Builder builder, RateProperties properties) {
        RateProperties.CoinGecko settings = properties.coingecko();
        builder.baseUrl(settings.baseUrl());
        if (StringUtils.hasText(settings.apiKey())) {
            builder.defaultHeader("x-cg-demo-api-key", settings.apiKey());
        }
        this.restClient = builder.build();
        this.idsByCode = new HashMap<>();
        if (settings.ids() != null) {
            settings.ids().forEach((code, id) -> idsByCode.put(code.toUpperCase(Locale.ROOT), id));
        }
    }

    @Override
    public String name() {
        return "CoinGecko";
    }

    @Override
    public Map<String, BigDecimal> fetchUsdRates() {
        if (idsByCode.isEmpty()) {
            return Map.of();
        }
        Map<String, Map<String, BigDecimal>> prices = restClient.get()
                .uri(uri -> uri.path("/simple/price")
                        .queryParam("ids", String.join(",", idsByCode.values()))
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (prices == null) {
            throw new IllegalStateException("CoinGecko returned an empty response");
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        idsByCode.forEach((code, id) -> {
            Map<String, BigDecimal> price = prices.get(id);
            if (price != null && price.get("usd") != null && price.get("usd").signum() > 0) {
                rates.put(code, price.get("usd").setScale(SCALE, RoundingMode.HALF_EVEN));
            }
        });
        return rates;
    }
}
