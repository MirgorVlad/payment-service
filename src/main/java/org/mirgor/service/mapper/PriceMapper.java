package org.mirgor.service.mapper;

import org.mirgor.common.dto.Price;
import org.mirgor.entity.PriceEntity;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper {

    public Price toDto(PriceEntity priceEntity) {
        return Price.builder()
                .id(priceEntity.getId())
                .workspaceId(priceEntity.getWorkspace().getId())
                .snapshotEntityType(priceEntity.getSnapshotEntityType())
                .currency(priceEntity.getCurrency())
                .price(priceEntity.getPrice())
                .build();
    }
}
