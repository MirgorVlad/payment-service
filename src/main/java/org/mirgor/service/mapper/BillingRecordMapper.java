package org.mirgor.service.mapper;

import org.mirgor.common.dto.BillingRecord;
import org.mirgor.entity.BillingRecordEntity;
import org.springframework.stereotype.Component;

@Component
public class BillingRecordMapper implements EntityMapper<BillingRecordEntity, BillingRecord> {

    @Override
    public BillingRecordEntity fromDto(BillingRecord dto) {
        return BillingRecordEntity.builder()
                .deviceCount(dto.getDeviceCount())
                .assetCount(dto.getAssetCount())
                .customerCount(dto.getCustomerCount())
                .startBillingPeriod(dto.getStartBillingPeriod())
                .endBillingPeriod(dto.getEndBillingPeriod())
                .build();
    }

    @Override
    public BillingRecord toDto(BillingRecordEntity entity) {
        return BillingRecord.builder()
                .id(entity.getId())
                .workspaceId(entity.getWorkspace().getId())
                .deviceCount(entity.getDeviceCount())
                .assetCount(entity.getAssetCount())
                .customerCount(entity.getCustomerCount())
                .startBillingPeriod(entity.getStartBillingPeriod())
                .endBillingPeriod(entity.getEndBillingPeriod())
                .build();
    }
}
