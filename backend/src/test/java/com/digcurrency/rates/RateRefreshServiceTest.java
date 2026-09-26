package com.digcurrency.rates;

import com.digcurrency.currency.Currency;
import com.digcurrency.currency.CurrencyRepository;
import com.digcurrency.currency.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateRefreshServiceTest {

    @Mock
    private CurrencyRepository repository;

    private final Currency btc = new Currency("BTC", "Bitcoin", CurrencyType.CRYPTO, new BigDecimal("65000"));
    private final Currency eur = new Currency("EUR", "Euro", CurrencyType.FIAT, new BigDecimal("1.08"));
    private final Currency edeur = new Currency("EDEUR", "Digital Euro", CurrencyType.CBDC, new BigDecimal("1.08"));
    private final Currency custom = new Currency("ABC", "Custom", CurrencyType.CRYPTO, new BigDecimal("2"));

    private RateRefreshService service(RateProvider... providers) {
        return new RateRefreshService(List.of(providers), repository,
                new RateProperties(true, null, null, null, Map.of("EDEUR", "EUR")));
    }

    private static RateProvider provider(String name, Map<String, BigDecimal> rates) {
        return new RateProvider() {
            public String name() {
                return name;
            }

            public Map<String, BigDecimal> fetchUsdRates() {
                return rates;
            }
        };
    }

    private static RateProvider failing(String name) {
        return new RateProvider() {
            public String name() {
                return name;
            }

            public Map<String, BigDecimal> fetchUsdRates() {
                throw new IllegalStateException("down");
            }
        };
    }

    @Test
    void updatesKnownCurrenciesAndPeggedOnes() {
        when(repository.findAllByOrderByCodeAsc()).thenReturn(List.of(btc, edeur, eur, custom));

        RateRefreshResult result = service(
                provider("CoinGecko", Map.of("BTC", new BigDecimal("84000"))),
                provider("ECB", Map.of("EUR", new BigDecimal("1.14")))).refresh();

        assertThat(result.updated()).containsExactly("BTC", "EDEUR", "EUR");
        assertThat(result.failedSources()).isEmpty();
        assertThat(btc.getUsdRate()).isEqualByComparingTo("84000");
        assertThat(eur.getUsdRate()).isEqualByComparingTo("1.14");
        assertThat(edeur.getUsdRate()).isEqualByComparingTo("1.14");
        assertThat(custom.getUsdRate()).isEqualByComparingTo("2");
    }

    @Test
    void keepsPreviousRatesWhenASourceFails() {
        when(repository.findAllByOrderByCodeAsc()).thenReturn(List.of(btc, eur));

        RateRefreshResult result = service(
                failing("CoinGecko"),
                provider("ECB", Map.of("EUR", new BigDecimal("1.14")))).refresh();

        assertThat(result.updated()).containsExactly("EUR");
        assertThat(result.failedSources()).containsExactly("CoinGecko");
        assertThat(btc.getUsdRate()).isEqualByComparingTo("65000");
        assertThat(eur.getUsdRate()).isEqualByComparingTo("1.14");
    }
}
