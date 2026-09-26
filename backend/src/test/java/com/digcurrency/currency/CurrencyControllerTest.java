package com.digcurrency.currency;

import com.digcurrency.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CurrencyService service;

    private static final CurrencyDto EUR =
            new CurrencyDto("EUR", "Euro", CurrencyType.FIAT, new BigDecimal("1.08"), Instant.EPOCH);

    @Test
    void listReturnsCurrencies() throws Exception {
        given(service.findAll(null)).willReturn(List.of(EUR));

        mvc.perform(get("/api/v1/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("EUR"))
                .andExpect(jsonPath("$[0].type").value("FIAT"));
    }

    @Test
    void getUnknownCurrencyReturns404ProblemDetail() throws Exception {
        given(service.findByCode("XXX")).willThrow(new NotFoundException("Currency XXX not found"));

        mvc.perform(get("/api/v1/currencies/XXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Currency XXX not found"));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        given(service.create(any())).willReturn(EUR);

        mvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"EUR","name":"Euro","type":"FIAT","usdRate":1.08}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/currencies/EUR"))
                .andExpect(jsonPath("$.code").value("EUR"));
    }

    @Test
    void createWithInvalidBodyReturns400() throws Exception {
        mvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"eur","name":"","type":"FIAT","usdRate":-1}
                                """))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }
}
