package com.digcurrency.conversion;

import java.math.BigDecimal;

public record ConversionResult(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal result) {
}
