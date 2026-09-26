package com.digcurrency.rates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EcbRateProviderTest {

    private static final String URL = "https://ecb.test/eurofxref-daily.xml";
    private static final String XML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01"
                             xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
              <gesmes:subject>Reference rates</gesmes:subject>
              <Cube>
                <Cube time="2026-09-25">
                  <Cube currency="USD" rate="1.2"/>
                  <Cube currency="GBP" rate="0.8"/>
                </Cube>
              </Cube>
            </gesmes:Envelope>
            """;

    private MockRestServiceServer server;
    private EcbRateProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        provider = new EcbRateProvider(builder,
                new RateProperties(true, null, null, new RateProperties.Ecb(URL), Map.of()));
    }

    @Test
    void convertsEuroQuotesToUsdRates() {
        server.expect(requestTo(URL)).andRespond(withSuccess(XML, MediaType.APPLICATION_XML));

        Map<String, BigDecimal> rates = provider.fetchUsdRates();

        assertThat(rates.get("EUR")).isEqualByComparingTo("1.2");
        assertThat(rates.get("USD")).isEqualByComparingTo("1");
        assertThat(rates.get("GBP")).isEqualByComparingTo("1.5"); // 1.2 USD per EUR / 0.8 GBP per EUR
        server.verify();
    }

    @Test
    void failsWhenUsdRateIsMissing() {
        String noUsd = XML.replace("<Cube currency=\"USD\" rate=\"1.2\"/>", "");
        server.expect(requestTo(URL)).andRespond(withSuccess(noUsd, MediaType.APPLICATION_XML));

        assertThatThrownBy(provider::fetchUsdRates)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no USD rate");
    }

    @Test
    void failsOnServerError() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(provider::fetchUsdRates).isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void rejectsDoctypeDeclarations() {
        String xxe = "<?xml version=\"1.0\"?><!DOCTYPE x [<!ENTITY e SYSTEM \"file:///etc/passwd\">]><x>&e;</x>";

        assertThatThrownBy(() -> EcbRateProvider.parse(xxe)).isInstanceOf(IllegalStateException.class);
    }
}
