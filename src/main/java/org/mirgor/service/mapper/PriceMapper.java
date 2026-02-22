package org.mirgor.service.mapper;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.PriceDto;
import org.mirgor.entity.Price;
import org.mirgor.service.WorkspaceService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceMapper implements EntityMapper<Price, PriceDto> {

    private final WorkspaceService workspaceService;

    public Price fromDto(PriceDto priceDto) {
        var workspace = workspaceService.getWorkspaceById(priceDto.getWorkspaceId());
        if (workspace == null) {
            throw new IllegalArgumentException(String.format("Workspace with id %s not found", priceDto.getWorkspaceId()));
        }
        return Price.builder()
                .workspace(workspace)
                .operationalEntityType(priceDto.getOperationalEntityType())
                .currency(priceDto.getCurrency())
                .price(priceDto.getPrice())
                .build();
    }

    public PriceDto toDto(Price price) {
        return PriceDto.builder()
                .id(price.getId())
                .workspaceId(price.getWorkspace().getId())
                .operationalEntityType(price.getOperationalEntityType())
                .currency(price.getCurrency())
                .price(price.getPrice())
                .build();
    }
}
