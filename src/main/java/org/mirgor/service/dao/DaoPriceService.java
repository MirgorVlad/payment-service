package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.entity.Price;
import org.mirgor.entity.PriceEntity;
import org.mirgor.repository.PriceRepository;
import org.mirgor.service.mapper.PriceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoPriceService {

    private final PriceRepository priceRepository;
    private final DaoWorkspaceService daoWorkspaceService;
    private final PriceMapper priceMapper;

    @Transactional
    public Price savePrice(Price price) {
        var workspaceEntity = daoWorkspaceService.findWorkspaceEntityById(price.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + price.getWorkspaceId()));
        PriceEntity entity;
        if (price.getId() != null) {
            entity = priceRepository.findById(price.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Price not found: " + price.getId()));
            entity.setWorkspace(workspaceEntity);
            entity.setWorkspaceEntityType(price.getWorkspaceEntityType());
            entity.setCurrency(price.getCurrency());
            entity.setPrice(price.getPrice());
        } else {
            entity = priceMapper.fromDto(price);
            entity.setWorkspace(workspaceEntity);
        }
        return priceMapper.toDto(priceRepository.save(entity));
    }

    @Transactional
    public void deletePrice(Long id) {
        priceRepository.deleteById(id);
    }

    public Optional<Price> findPriceById(Long id) {
        return priceRepository.findById(id).map(priceMapper::toDto);
    }

    public Optional<Price> findByIdAndWorkspaceUserId(Long id, Long userId) {
        return priceRepository.findByIdAndWorkspaceUserId(id, userId).map(priceMapper::toDto);
    }

    public boolean existsByIdAndWorkspaceUserId(Long id, Long userId) {
        return priceRepository.existsByIdAndWorkspaceUserId(id, userId);
    }

    public List<Price> findByWorkspaceId(Long workspaceId) {
        return priceRepository.findByWorkspaceId(workspaceId).stream().map(priceMapper::toDto).toList();
    }

    public List<Price> findByWorkspaceUserId(Long userId) {
        return priceRepository.findByWorkspaceUserId(userId).stream().map(priceMapper::toDto).toList();
    }

    public BigDecimal findLatestByWorkspaceIdAndEntityType(Long workspaceId, WorkspaceEntityType type) {
        return priceRepository.findLatestByWorkspaceIdAndEntityType(workspaceId, type);
    }
}
