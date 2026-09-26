package com.digcurrency.conversion;

import com.digcurrency.currency.CurrencyDto;
import com.digcurrency.currency.CurrencyService;
import com.digcurrency.currency.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    CurrencyService currencyService;

    @InjectMocks
    ConversionService conversionService;

    @Test
    void convertsViaUsdRates() {
        given(currencyService.findByCode("BTC")).willReturn(
                new CurrencyDto("BTC", "Bitcoin", CurrencyType.CRYPTO, new BigDecimal("65000"), Instant.EPOCH));
        given(currencyService.findByCode("EUR")).willReturn(
                new CurrencyDto("EUR", "Euro", CurrencyType.FIAT, new BigDecimal("1.25"), Instant.EPOCH));

        ConversionResult result = conversionService.convert("BTC", "EUR", new BigDecimal("0.5"));

        assertThat(result.result()).isEqualByComparingTo("26000");
        assertThat(result.rate()).isEqualByComparingTo("52000");
        assertThat(result.result().scale()).isEqualTo(ConversionService.SCALE);
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> conversionService.convert("BTC", "EUR", new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
