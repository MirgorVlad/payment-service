package org.mirgor.service.billing.pricing_strategy;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.service.dao.DaoSnapshotService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PeakUsagePricing implements PricingStrategy {

    private final DaoSnapshotService daoSnapshotService;

    @Override
    public Long calculate(Long workspaceId, WorkspaceEntityType type, TimeInterval timeInterval) {
        return daoSnapshotService.findMaxEntityCountByWorkspaceAndPeriod(workspaceId, type, timeInterval)
                .orElse(0L);
    }

    @Override
    public PricingStrategyType getPricingStrategyType() {
        return PricingStrategyType.MAX;
    }
}
