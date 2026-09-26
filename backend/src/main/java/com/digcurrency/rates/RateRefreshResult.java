package com.digcurrency.rates;

import java.util.List;

/**
 * Outcome of one rate refresh.
 *
 * @param updated       codes of the currencies that got a new rate
 * @param failedSources names of the sources that could not be read
 */
public record RateRefreshResult(List<String> updated, List<String> failedSources) {
}
