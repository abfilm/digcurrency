package com.digcurrency.currency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CurrencyRequest(
        @NotBlank @Pattern(regexp = "[A-Z0-9]{2,10}", message = "must be 2-10 uppercase letters or digits")
        String code,
        @NotBlank @Size(max = 100)
        String name,
        @NotNull
        CurrencyType type,
        @NotNull @DecimalMin(value = "0", inclusive = false)
        BigDecimal usdRate) {
}
