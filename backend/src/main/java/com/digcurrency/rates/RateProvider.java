package com.digcurrency.rates;

import java.math.BigDecimal;
import java.util.Map;

/** A live source of exchange rates. */
interface RateProvider {

    /** Scale used for every rate a provider returns; matches the {@code usdRate} column. */
    int SCALE = 10;

    /** Short name for logs and results, e.g. {@code "ECB"}. */
    String name();

    /**
     * Fetches current rates.
     *
     * @return USD value of one unit, keyed by upper-case currency code
     * @throws RuntimeException if the source can't be reached or returns unusable data
     */
    Map<String, BigDecimal> fetchUsdRates();
}
