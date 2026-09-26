package com.digcurrency.conversion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/conversions")
@Tag(name = "Conversions")
public class ConversionController {

    private final ConversionService service;

    public ConversionController(ConversionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Convert an amount between two currencies")
    public ConversionResult convert(@RequestParam String from,
                                    @RequestParam String to,
                                    @RequestParam BigDecimal amount) {
        return service.convert(from, to, amount);
    }
}
