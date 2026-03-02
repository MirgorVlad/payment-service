package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.Role;
import org.mirgor.common.dto.BillingRecord;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.dao.DaoBillingRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingRecordService {

    private final DaoBillingRecordService daoBillingRecordService;

    public BillingRecord createBillingRecord(BillingRecord billingRecord) {
        billingRecord.setId(null);
        return daoBillingRecordService.saveBillingRecord(billingRecord);
    }

    public BillingRecord updateBillingRecord(Long id, BillingRecord updatedBillingRecord) {
        var userId = SecurityUtil.getCurrentUserId();
        var existing = daoBillingRecordService.findByIdAndWorkspaceUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("BillingRecord not found with id: " + id));
        existing.setDeviceCount(updatedBillingRecord.getDeviceCount());
        existing.setAssetCount(updatedBillingRecord.getAssetCount());
        existing.setCustomerCount(updatedBillingRecord.getCustomerCount());
        existing.setStartBillingPeriod(updatedBillingRecord.getStartBillingPeriod());
        existing.setEndBillingPeriod(updatedBillingRecord.getEndBillingPeriod());
        existing.setWorkspaceId(updatedBillingRecord.getWorkspaceId());
        return daoBillingRecordService.saveBillingRecord(existing);
    }

    public void deleteBillingRecord(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!daoBillingRecordService.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new IllegalArgumentException("BillingRecord not found with id: " + id);
        }
        daoBillingRecordService.deleteBillingRecord(id);
    }

    public BillingRecord getBillingRecordById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (SecurityUtil.getCurrentUserRole().equals(Role.ADMIN)) {
            return daoBillingRecordService.findBillingRecordById(id)
                    .orElseThrow(() -> new IllegalArgumentException("BillingRecord not found with id: " + id));
        }
        return daoBillingRecordService.findByIdAndWorkspaceUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("BillingRecord not found with id: " + id));
    }

    public List<BillingRecord> getAllBillingRecords(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return daoBillingRecordService.findByWorkspaceId(workspaceId);
        }
        if (SecurityUtil.getCurrentUserRole().equals(Role.ADMIN)) {
            return daoBillingRecordService.findAllBillingRecords();
        }
        return daoBillingRecordService.findByWorkspaceUserId(userId);
    }
}
