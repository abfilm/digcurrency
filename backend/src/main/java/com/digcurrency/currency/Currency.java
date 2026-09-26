package com.digcurrency.currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Currency {

    @Id
    @Column(length = 10)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType type;

    /** Value of one unit of this currency in USD. */
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal usdRate;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Currency() {
    }

    public Currency(String code, String name, CurrencyType type, BigDecimal usdRate) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.usdRate = usdRate;
        this.updatedAt = Instant.now();
    }

    public void update(String name, CurrencyType type, BigDecimal usdRate) {
        this.name = name;
        this.type = type;
        this.usdRate = usdRate;
        this.updatedAt = Instant.now();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public CurrencyType getType() {
        return type;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
