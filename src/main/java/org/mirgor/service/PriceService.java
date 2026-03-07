package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.entity.Price;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.dao.DaoPriceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final DaoPriceService daoPriceService;

    public Price createPrice(Price price) {
        price.setId(null);
        return daoPriceService.savePrice(price);
    }

    public Price updatePrice(Long id, Price updatedPrice) {
        var existing = getPriceById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Price with id %s is not found", id)));

        existing.setWorkspaceId(updatedPrice.getWorkspaceId());
        existing.setPrice(updatedPrice.getPrice());
        existing.setCurrency(updatedPrice.getCurrency());
        existing.setWorkspaceEntityType(updatedPrice.getWorkspaceEntityType());

        return daoPriceService.savePrice(existing);
    }

    public void deletePrice(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!daoPriceService.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new RuntimeException("Price not found with id: " + id);
        }
        daoPriceService.deletePrice(id);
    }

    public Optional<Price> getPriceById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return daoPriceService.findByIdAndWorkspaceUserId(id, userId);
    }

    public List<Price> getAllPrices(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return daoPriceService.findByWorkspaceId(workspaceId);
        }
        return daoPriceService.findByWorkspaceUserId(userId);
    }
}
