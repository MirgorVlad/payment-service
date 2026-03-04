package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.workspace.TimeInterval;
import org.mirgor.common.entity.BillingRecord;
import org.mirgor.service.dao.DaoBillingRecordService;
import org.mirgor.service.dao.DaoPriceService;
import org.mirgor.service.dao.DaoSnapshotService;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.mirgor.service.utils.TimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final DaoPriceService daoPriceService;
    private final DaoWorkspaceService daoWorkspaceService;
    private final DaoBillingRecordService daoBillingRecordService;
    private final DaoSnapshotService daoSnapshotService;

    @Scheduled(cron = "0 0 0 L * ?")
    private void generateMonthlyBillingRecords() {
        var workspaces = daoWorkspaceService.findAllWorkspaces();
        var timeRange = TimeUtil.getPrevMonthRange(ZoneOffset.systemDefault());

        workspaces.forEach(workspace -> {
            generateBillingRecord(workspace.getId(), timeRange);
        });
    }

    private BillingRecord generateBillingRecord(Long workspaceId, TimeInterval timeInterval) {
        var entityCountMap = Arrays.stream(WorkspaceEntityType.values())
                .collect(Collectors.toMap(Function.identity(),
                        we -> {
                            var count = daoSnapshotService.findMaxEntityCountByWorkspaceAndPeriod(workspaceId, we, timeInterval); //TODO implement different strategies [Max, avg, latest]
                            var price = daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, we);
                            return new EntityCountPrice(count, price);
                        }));


        var billingRecord = buildBillingRecord(workspaceId, timeInterval, entityCountMap);
        return daoBillingRecordService.saveBillingRecord(billingRecord);
    }

    private BillingRecord buildBillingRecord(Long workspaceId, TimeInterval timeInterval, Map<WorkspaceEntityType, EntityCountPrice> entityCountMap) {
        return BillingRecord.builder()
                .workspaceId(workspaceId)
                .assetCount(entityCountMap.get(WorkspaceEntityType.ASSET).count)
                .deviceCount(entityCountMap.get(WorkspaceEntityType.DEVICE).count)
                .customerCount(entityCountMap.get(WorkspaceEntityType.CUSTOMER).count)
                .assetPrice(entityCountMap.get(WorkspaceEntityType.ASSET).total())
                .devicePrice(entityCountMap.get(WorkspaceEntityType.DEVICE).total())
                .customerPrice(entityCountMap.get(WorkspaceEntityType.CUSTOMER).total())
                .startBillingPeriod(timeInterval.startTime())
                .endBillingPeriod(timeInterval.startTime())
                .build();
    }

    record EntityCountPrice(Long count, BigDecimal price) {
        BigDecimal total() {
            return price.multiply(BigDecimal.valueOf(count));
        }
    }
}
