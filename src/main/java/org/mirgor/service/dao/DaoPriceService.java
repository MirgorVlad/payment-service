package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.Price;
import org.mirgor.entity.PriceEntity;
import org.mirgor.repository.PriceRepository;
import org.mirgor.service.mapper.PriceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoPriceService {

    private final PriceRepository priceRepository;
    private final DaoWorkspaceService daoWorkspaceService;
    private final PriceMapper priceMapper;

    @Transactional
    public Price savePrice(Price dto) {
        var workspaceEntity = daoWorkspaceService.findWorkspaceEntityById(dto.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + dto.getWorkspaceId()));
        PriceEntity entity;
        if (dto.getId() != null) {
            entity = priceRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Price not found: " + dto.getId()));
            entity.setWorkspace(workspaceEntity);
            entity.setSnapshotEntityType(dto.getSnapshotEntityType());
            entity.setCurrency(dto.getCurrency());
            entity.setPrice(dto.getPrice());
        } else {
            entity = PriceEntity.builder()
                    .workspace(workspaceEntity)
                    .snapshotEntityType(dto.getSnapshotEntityType())
                    .currency(dto.getCurrency())
                    .price(dto.getPrice())
                    .build();
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
}
