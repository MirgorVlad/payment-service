package org.mirgor.service.mapper;

import org.mirgor.common.entity.BillingRecord;
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
                .devicePrice(dto.getDevicePrice())
                .assetPrice(dto.getAssetPrice())
                .customerPrice(dto.getCustomerPrice())
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
                .devicePrice(entity.getDevicePrice())
                .assetPrice(entity.getAssetPrice())
                .customerPrice(entity.getCustomerPrice())
                .customerCount(entity.getCustomerCount())
                .startBillingPeriod(entity.getStartBillingPeriod())
                .endBillingPeriod(entity.getEndBillingPeriod())
                .build();
    }
}
