package org.mirgor.service.billing;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.Role;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.EntityCountPrice;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.BillingRecord;
import org.mirgor.exception.BillingException;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.billing.pricing_strategy.PricingStrategy;
import org.mirgor.service.dao.DaoBillingRecordService;
import org.mirgor.service.dao.DaoPriceService;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.mirgor.service.utils.TimeUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final DaoBillingRecordService daoBillingRecordService;
    private final DaoWorkspaceService daoWorkspaceService;
    private final DaoPriceService daoPriceService;

    private final List<PricingStrategy> pricingStrategyList;
    private Map<PricingStrategyType, PricingStrategy> pricingStrategyMap;

    @PostConstruct
    private void initStrategyMap() {
        pricingStrategyMap = pricingStrategyList.stream()
                .collect(Collectors.toMap(
                        PricingStrategy::getPricingStrategyType,
                        Function.identity()
                ));
    }

    @Scheduled(cron = "0 0 0 L * ?")
    void generateMonthlyBillingRecords() {
        var workspaces = daoWorkspaceService.findAllWorkspaces();
        var timeRange = TimeUtil.getPrevMonthRange(ZoneOffset.systemDefault());

        workspaces.forEach(workspace -> {
            generateBillingRecord(workspace.getId(), timeRange);
        });
    }

    @Async("dbTaskExecutor")
    public CompletableFuture<BillingRecord> generateBillingRecord(Long workspaceId, TimeInterval timeInterval) {
        var workspace = daoWorkspaceService.findWorkspaceById(workspaceId).orElseThrow(() -> new IllegalArgumentException(String.format("Workspace %s is not found", workspaceId)));
        var entityCountMap = Arrays.stream(WorkspaceEntityType.values())
                .collect(Collectors.toMap(Function.identity(),
                        we -> {
                            var count = pricingStrategyMap.get(workspace.getPricingStrategy()).calculate(workspaceId, we, timeInterval);
                            var price = daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, we)
                                    .orElseThrow(() -> new BillingException(String.format("Price is not found for workspace %s and entity %s", workspaceId, we)));
                            return new EntityCountPrice(count, price.getPrice());
                        }));


        var billingRecord = buildBillingRecord(workspaceId, timeInterval, entityCountMap, workspace.getCurrency());
        return CompletableFuture.completedFuture(daoBillingRecordService.saveBillingRecord(billingRecord));
    }

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

    private BillingRecord buildBillingRecord(Long workspaceId, TimeInterval timeInterval,
                                             Map<WorkspaceEntityType, EntityCountPrice> entityCountMap,
                                             Currency currency) {
        return BillingRecord.builder()
                .workspaceId(workspaceId)
                .assetCount(entityCountMap.get(WorkspaceEntityType.ASSET).count())
                .deviceCount(entityCountMap.get(WorkspaceEntityType.DEVICE).count())
                .customerCount(entityCountMap.get(WorkspaceEntityType.CUSTOMER).count())
                .assetPrice(entityCountMap.get(WorkspaceEntityType.ASSET).total())
                .devicePrice(entityCountMap.get(WorkspaceEntityType.DEVICE).total())
                .customerPrice(entityCountMap.get(WorkspaceEntityType.CUSTOMER).total())
                .currency(currency)
                .startBillingPeriod(timeInterval.startTime())
                .endBillingPeriod(timeInterval.endTime())
                .build();
    }
}
