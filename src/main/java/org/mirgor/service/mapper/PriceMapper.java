package org.mirgor.service.mapper;

import org.mirgor.common.entity.Price;
import org.mirgor.entity.PriceEntity;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper implements EntityMapper<PriceEntity, Price> {

    @Override
    public Price toDto(PriceEntity priceEntity) {
        return Price.builder()
                .id(priceEntity.getId())
                .workspaceId(priceEntity.getWorkspace().getId())
                .workspaceEntityType(priceEntity.getWorkspaceEntityType())
                .currency(priceEntity.getCurrency())
                .price(priceEntity.getPrice())
                .build();
    }

    @Override
    public PriceEntity fromDto(Price price) {
        return PriceEntity.builder()
                .workspaceEntityType(price.getWorkspaceEntityType())
                .currency(price.getCurrency())
                .price(price.getPrice())
                .build();
    }
}
