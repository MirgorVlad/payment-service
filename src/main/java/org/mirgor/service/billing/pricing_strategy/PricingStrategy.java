package org.mirgor.service.billing.pricing_strategy;

import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;

public interface PricingStrategy {

    Long calculate(Long id, WorkspaceEntityType type, TimeInterval timeInterval);

    PricingStrategyType getPricingStrategyType();
}
