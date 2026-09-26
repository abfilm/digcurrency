package com.digcurrency.currency;

import java.math.BigDecimal;
import java.time.Instant;

public record CurrencyDto(
        String code,
        String name,
        CurrencyType type,
        BigDecimal usdRate,
        Instant updatedAt) {

    static CurrencyDto from(Currency currency) {
        return new CurrencyDto(
                currency.getCode(),
                currency.getName(),
                currency.getType(),
                currency.getUsdRate(),
                currency.getUpdatedAt());
    }
}
