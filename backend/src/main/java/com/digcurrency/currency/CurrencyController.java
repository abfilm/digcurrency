package com.digcurrency.currency;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currencies")
public class CurrencyController {

    private final CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List currencies, optionally filtered by type")
    public List<CurrencyDto> list(@RequestParam(required = false) CurrencyType type) {
        return service.findAll(type);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get a single currency")
    public CurrencyDto get(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    @Operation(summary = "Create a currency")
    public ResponseEntity<CurrencyDto> create(@Valid @RequestBody CurrencyRequest request) {
        CurrencyDto created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}").buildAndExpand(created.code()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a currency")
    public CurrencyDto update(@PathVariable String code, @Valid @RequestBody CurrencyRequest request) {
        return service.update(code, request);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a currency")
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}
