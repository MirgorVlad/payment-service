package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.BillingRecord;
import org.mirgor.entity.BillingRecordEntity;
import org.mirgor.repository.BillingRecordRepository;
import org.mirgor.service.mapper.BillingRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoBillingRecordService {

    private final BillingRecordRepository billingRecordRepository;
    private final DaoWorkspaceService daoWorkspaceService;
    private final BillingRecordMapper billingRecordMapper;

    @Transactional
    public BillingRecord saveBillingRecord(BillingRecord billingRecord) {
        var workspaceEntity = daoWorkspaceService.findWorkspaceEntityById(billingRecord.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + billingRecord.getWorkspaceId()));
        BillingRecordEntity entity;
        if (billingRecord.getId() != null) {
            entity = billingRecordRepository.findById(billingRecord.getId())
                    .orElseThrow(() -> new IllegalArgumentException("BillingRecord not found: " + billingRecord.getId()));
            entity.setDeviceCount(billingRecord.getDeviceCount());
            entity.setAssetCount(billingRecord.getAssetCount());
            entity.setCustomerCount(billingRecord.getCustomerCount());
            entity.setStartBillingPeriod(billingRecord.getStartBillingPeriod());
            entity.setEndBillingPeriod(billingRecord.getEndBillingPeriod());
            entity.setWorkspace(workspaceEntity);
        } else {
            entity = billingRecordMapper.fromDto(billingRecord);
            entity.setWorkspace(workspaceEntity);
        }
        return billingRecordMapper.toDto(billingRecordRepository.save(entity));
    }

    @Transactional
    public void deleteBillingRecord(Long id) {
        billingRecordRepository.deleteById(id);
    }

    public Optional<BillingRecord> findBillingRecordById(Long id) {
        return billingRecordRepository.findById(id).map(billingRecordMapper::toDto);
    }

    public Optional<BillingRecord> findByIdAndWorkspaceUserId(Long id, Long userId) {
        return billingRecordRepository.findByIdAndWorkspaceUserId(id, userId).map(billingRecordMapper::toDto);
    }

    public List<BillingRecord> findAllBillingRecords() {
        return billingRecordRepository.findAll().stream().map(billingRecordMapper::toDto).toList();
    }

    public List<BillingRecord> findByWorkspaceId(Long workspaceId) {
        return billingRecordRepository.findByWorkspaceId(workspaceId).stream()
                .map(billingRecordMapper::toDto)
                .toList();
    }

    public List<BillingRecord> findByWorkspaceUserId(Long userId) {
        return billingRecordRepository.findByWorkspaceUserId(userId).stream()
                .map(billingRecordMapper::toDto)
                .toList();
    }

    public boolean existsByIdAndWorkspaceUserId(Long id, Long userId) {
        return billingRecordRepository.existsByIdAndWorkspaceUserId(id, userId);
    }
}
