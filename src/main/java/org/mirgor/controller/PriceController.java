package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.dto.PriceDto;
import org.mirgor.service.PriceService;
import org.mirgor.service.mapper.PriceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;
    private final PriceMapper mapper;

    @PostMapping
    public ResponseEntity<PriceDto> createPrice(@RequestBody @Valid PriceDto priceDto) {
        var price = mapper.fromDto(priceDto);
        var createdPrice = priceService.createPrice(price);
        return new ResponseEntity<>(mapper.toDto(createdPrice), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PriceDto>> getAllPrices(@RequestParam(required = false) Long workspaceId) {
        var prices = priceService.getAllPrices(workspaceId);
        var priceDtos = prices.stream()
                .map(mapper::toDto)
                .toList();
        return new ResponseEntity<>(priceDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceDto> getPriceById(@PathVariable Long id) {
        return priceService.getPriceById(id)
                .map(mapper::toDto)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceDto> updatePrice(@PathVariable Long id, @RequestBody @Valid PriceDto priceDto) {
        var updatedPrice = priceService.updatePrice(id, mapper.fromDto(priceDto));
        var updatedPriceDto = mapper.toDto(updatedPrice);
        return ResponseEntity.ok(updatedPriceDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        priceService.deletePrice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
