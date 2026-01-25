package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Price;
import org.mirgor.repository.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    @Transactional
    public Price createPrice(Price price) {
        if (price.getId() != null && priceRepository.existsById(price.getId())) {
            throw new RuntimeException("Price ID already exists: " + price.getId());
        }

        return priceRepository.save(price);
    }

    public Optional<Price> getPriceById(Long id) {
        return priceRepository.findById(id);
    }

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    @Transactional
    public Price updatePrice(Long id, Price updatedPrice) {
        Price existingPrice = priceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price not found with id: " + id));

        if (updatedPrice.getWorkspace() != null) {
            existingPrice.setWorkspace(updatedPrice.getWorkspace());
        }
        if (updatedPrice.getEntityType() != null) {
            existingPrice.setEntityType(updatedPrice.getEntityType());
        }
        if (updatedPrice.getCurrency() != null) {
            existingPrice.setCurrency(updatedPrice.getCurrency());
        }
        if (updatedPrice.getPrice() != null) {
            existingPrice.setPrice(updatedPrice.getPrice());
        }

        return priceRepository.save(existingPrice);
    }

    @Transactional
    public void deletePrice(Long id) {
        if (!priceRepository.existsById(id)) {
            throw new RuntimeException("Price not found with id: " + id);
        }
        priceRepository.deleteById(id);
    }
}
