package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Price;
import org.mirgor.repository.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoPriceService {

    private final PriceRepository priceRepository;

    @Transactional
    public Price savePrice(Price price) {
        return priceRepository.save(price);
    }

    @Transactional
    public void deletePrice(Long id) {
        priceRepository.deleteById(id);
    }

    public Optional<Price> findPriceById(Long id) {
        return priceRepository.findById(id);
    }

    public Optional<Price> findByIdAndWorkspaceUserId(Long id, Long userId) {
        return priceRepository.findByIdAndWorkspaceUserId(id, userId);
    }

    public boolean existsByIdAndWorkspaceUserId(Long id, Long userId) {
        return priceRepository.existsByIdAndWorkspaceUserId(id, userId);
    }

    public List<Price> findByWorkspaceId(Long workspaceId) {
        return priceRepository.findByWorkspaceId(workspaceId);
    }

    public List<Price> findByWorkspaceUserId(Long userId) {
        return priceRepository.findByWorkspaceUserId(userId);
    }
}
