package com.digcurrency.rates;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Fiat rates from the European Central Bank's daily euro reference rates.
 * The ECB quotes "units per 1 EUR", so every rate is converted to "USD per unit" via the EUR/USD rate.
 * The ECB publishes once per working day, around 16:00 CET.
 */
@Component
class EcbRateProvider implements RateProvider {

    private final RestClient restClient;
    private final String url;

    EcbRateProvider(RestClient.Builder builder, RateProperties properties) {
        this.restClient = builder.build();
        this.url = properties.ecb().url();
    }

    @Override
    public String name() {
        return "ECB";
    }

    @Override
    public Map<String, BigDecimal> fetchUsdRates() {
        String xml = restClient.get().uri(url).retrieve().body(String.class);
        Map<String, BigDecimal> perEur = parse(xml);

        BigDecimal usdPerEur = perEur.get("USD");
        if (usdPerEur == null || usdPerEur.signum() <= 0) {
            throw new IllegalStateException("ECB response has no USD rate");
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", usdPerEur.setScale(SCALE, RoundingMode.HALF_EVEN));
        perEur.forEach((code, unitsPerEur) -> {
            if (unitsPerEur.signum() > 0) {
                rates.put(code, usdPerEur.divide(unitsPerEur, SCALE, RoundingMode.HALF_EVEN));
            }
        });
        return rates;
    }

    /** Reads {@code <Cube currency="X" rate="r"/>} entries into a map of units per 1 EUR. */
    static Map<String, BigDecimal> parse(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalStateException("ECB returned an empty response");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            NodeList cubes = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)))
                    .getElementsByTagNameNS("*", "Cube");

            Map<String, BigDecimal> perEur = new HashMap<>();
            for (int i = 0; i < cubes.getLength(); i++) {
                Element cube = (Element) cubes.item(i);
                if (cube.hasAttribute("currency") && cube.hasAttribute("rate")) {
                    perEur.put(cube.getAttribute("currency"), new BigDecimal(cube.getAttribute("rate")));
                }
            }
            return perEur;
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse ECB rates: " + e.getMessage(), e);
        }
    }
}
