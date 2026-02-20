package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Price;
import org.mirgor.repository.PriceRepository;
import org.mirgor.security.utils.SecurityUtil;
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
        price.setId(null);
        return priceRepository.save(price);
    }

    @Transactional
    public Price updatePrice(Long id, Price updatedPrice) {
        var priceOptional = getPriceById(id);
        var price = priceOptional.orElseThrow(() ->
                new IllegalArgumentException(String.format("Price with id %s is not found", id)));


        price.setWorkspace(updatedPrice.getWorkspace());
        price.setPrice(updatedPrice.getPrice());
        price.setCurrency(updatedPrice.getCurrency());
        price.setOperationalEntityType(updatedPrice.getOperationalEntityType());

        return priceRepository.save(price);
    }

    @Transactional
    public void deletePrice(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!priceRepository.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new RuntimeException("Price not found with id: " + id);
        }
        priceRepository.deleteById(id);
    }

    public Optional<Price> getPriceById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return priceRepository.findByIdAndWorkspaceUserId(id, userId);
    }

    public List<Price> getAllPrices(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return priceRepository.findByWorkspaceId(workspaceId);
        }
        return priceRepository.findByWorkspaceUserId(userId);
    }
}
