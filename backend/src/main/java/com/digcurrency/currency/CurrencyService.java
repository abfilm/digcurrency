package com.digcurrency.currency;

import com.digcurrency.common.ConflictException;
import com.digcurrency.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CurrencyService {

    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public List<CurrencyDto> findAll(CurrencyType type) {
        List<Currency> currencies = type == null
                ? repository.findAllByOrderByCodeAsc()
                : repository.findByTypeOrderByCodeAsc(type);
        return currencies.stream().map(CurrencyDto::from).toList();
    }

    public CurrencyDto findByCode(String code) {
        return CurrencyDto.from(getEntity(code));
    }

    @Transactional
    public CurrencyDto create(CurrencyRequest request) {
        if (repository.existsById(request.code())) {
            throw new ConflictException("Currency " + request.code() + " already exists");
        }
        Currency saved = repository.save(
                new Currency(request.code(), request.name(), request.type(), request.usdRate()));
        return CurrencyDto.from(saved);
    }

    @Transactional
    public CurrencyDto update(String code, CurrencyRequest request) {
        if (!code.equals(request.code())) {
            throw new IllegalArgumentException("Path code " + code + " does not match body code " + request.code());
        }
        Currency currency = getEntity(code);
        currency.update(request.name(), request.type(), request.usdRate());
        return CurrencyDto.from(currency);
    }

    @Transactional
    public void delete(String code) {
        repository.delete(getEntity(code));
    }

    Currency getEntity(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new NotFoundException("Currency " + code + " not found"));
    }
}
