package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.Price;
import org.mirgor.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;

    @PostMapping
    public ResponseEntity<Price> createPrice(@RequestBody @Valid Price priceDto) {
        return new ResponseEntity<>(priceService.createPrice(priceDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Price>> getAllPrices(@RequestParam(required = false) Long workspaceId) {
        return new ResponseEntity<>(priceService.getAllPrices(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Price> getPriceById(@PathVariable Long id) {
        return priceService.getPriceById(id)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Price> updatePrice(@PathVariable Long id, @RequestBody @Valid Price price) {
        return ResponseEntity.ok(priceService.updatePrice(id, price));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        priceService.deletePrice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
