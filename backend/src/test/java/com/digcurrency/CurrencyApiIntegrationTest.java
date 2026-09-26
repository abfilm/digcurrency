package com.digcurrency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full-stack test: HTTP -> controller -> service -> JPA -> H2. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CurrencyApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createConvertAndDelete() throws Exception {
        createCurrency("USD", "US Dollar", "FIAT", "1");
        createCurrency("ETH", "Ether", "CRYPTO", "3000");

        mvc.perform(get("/api/v1/conversions")
                        .param("from", "ETH").param("to", "USD").param("amount", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6000.0));

        mvc.perform(delete("/api/v1/currencies/ETH")).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/currencies/ETH")).andExpect(status().isNotFound());
    }

    @Test
    void duplicateCodeReturns409() throws Exception {
        createCurrency("USD", "US Dollar", "FIAT", "1");

        mvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"USD","name":"Dup","type":"FIAT","usdRate":1}
                                """))
                .andExpect(status().isConflict());
    }

    private void createCurrency(String code, String name, String type, String usdRate) throws Exception {
        mvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s","name":"%s","type":"%s","usdRate":%s}
                                """.formatted(code, name, type, usdRate)))
                .andExpect(status().isCreated());
    }
}
