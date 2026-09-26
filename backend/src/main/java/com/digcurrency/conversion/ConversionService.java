package com.digcurrency.conversion;

import com.digcurrency.currency.CurrencyDto;
import com.digcurrency.currency.CurrencyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class ConversionService {

    static final int SCALE = 8;

    private final CurrencyService currencyService;

    public ConversionService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public ConversionResult convert(String fromCode, String toCode, BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        CurrencyDto from = currencyService.findByCode(fromCode);
        CurrencyDto to = currencyService.findByCode(toCode);

        BigDecimal rate = from.usdRate().divide(to.usdRate(), MathContext.DECIMAL128)
                .setScale(SCALE, RoundingMode.HALF_EVEN);
        BigDecimal result = amount.multiply(from.usdRate())
                .divide(to.usdRate(), SCALE, RoundingMode.HALF_EVEN);

        return new ConversionResult(from.code(), to.code(), amount, rate, result);
    }
}
