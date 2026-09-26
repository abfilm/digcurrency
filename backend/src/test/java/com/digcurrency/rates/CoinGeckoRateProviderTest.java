package com.digcurrency.rates;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.startsWith;

class CoinGeckoRateProviderTest {

    private static final String BASE_URL = "https://coingecko.test/api/v3";

    private MockRestServiceServer server;

    private CoinGeckoRateProvider provider(String apiKey, Map<String, String> ids) {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        return new CoinGeckoRateProvider(builder, new RateProperties(true, null,
                new RateProperties.CoinGecko(BASE_URL, apiKey, ids), null, Map.of()));
    }

    @Test
    void mapsCoinPricesToCurrencyCodes() {
        Map<String, String> ids = new LinkedHashMap<>();
        ids.put("BTC", "bitcoin");
        ids.put("eth", "ethereum");
        CoinGeckoRateProvider provider = provider("", ids);
        server.expect(requestTo(startsWith(BASE_URL + "/simple/price")))
                .andExpect(queryParam("vs_currencies", "usd"))
                .andRespond(withSuccess("""
                        {"bitcoin":{"usd":84199.5},"ethereum":{"usd":2690.9}}
                        """, MediaType.APPLICATION_JSON));

        Map<String, BigDecimal> rates = provider.fetchUsdRates();

        assertThat(rates).containsOnlyKeys("BTC", "ETH");
        assertThat(rates.get("BTC")).isEqualByComparingTo("84199.5");
        assertThat(rates.get("ETH")).isEqualByComparingTo("2690.9");
        server.verify();
    }

    @Test
    void skipsCoinsMissingFromTheResponse() {
        CoinGeckoRateProvider provider = provider("", Map.of("BTC", "bitcoin", "XYZ", "unknown-coin"));
        server.expect(requestTo(startsWith(BASE_URL)))
                .andRespond(withSuccess("{\"bitcoin\":{\"usd\":1}}", MediaType.APPLICATION_JSON));

        assertThat(provider.fetchUsdRates()).containsOnlyKeys("BTC");
    }

    @Test
    void sendsApiKeyWhenConfigured() {
        CoinGeckoRateProvider provider = provider("secret", Map.of("BTC", "bitcoin"));
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header("x-cg-demo-api-key", "secret"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        provider.fetchUsdRates();

        server.verify();
    }

    @Test
    void failsWhenRateLimited() {
        CoinGeckoRateProvider provider = provider("", Map.of("BTC", "bitcoin"));
        server.expect(requestTo(startsWith(BASE_URL))).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(provider::fetchUsdRates).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void makesNoRequestWithoutConfiguredCoins() {
        CoinGeckoRateProvider provider = provider("", Map.of());

        assertThat(provider.fetchUsdRates()).isEmpty();
        server.verify();
    }
}
