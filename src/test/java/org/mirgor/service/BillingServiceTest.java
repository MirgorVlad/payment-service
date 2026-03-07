package org.mirgor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.BillingRecord;
import org.mirgor.common.dto.entity.Price;
import org.mirgor.service.dao.DaoBillingRecordService;
import org.mirgor.service.dao.DaoPriceService;
import org.mirgor.service.dao.DaoSnapshotService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private DaoBillingRecordService daoBillingRecordService;
    @Mock
    private DaoSnapshotService daoSnapshotService;
    @Mock
    private DaoPriceService daoPriceService;

    @InjectMocks
    private BillingService billingService;


    @Test
    @DisplayName("Should successfully generate billing record for workspace for selected period")
    public void shouldSuccessfullyCreateBillingRecord() throws ExecutionException, InterruptedException {
        //GIVEN
        var workspaceId = 1L;
        var timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        var entityCount = 5L;
        var assetPrice = new Price(1L, workspaceId, null, Currency.USD, BigDecimal.valueOf(5));
        var devicePrice = new Price(2L, workspaceId, null, Currency.USD, BigDecimal.valueOf(6));
        var customerPrice = new Price(3L, workspaceId, null, Currency.USD, BigDecimal.valueOf(7));

        when(daoSnapshotService.findMaxEntityCountByWorkspaceAndPeriod(eq(workspaceId), any(WorkspaceEntityType.class), eq(timeInterval)))
                .thenReturn(entityCount);
        when(daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, WorkspaceEntityType.ASSET))
                .thenReturn(Optional.of(assetPrice));
        when(daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, WorkspaceEntityType.DEVICE))
                .thenReturn(Optional.of(devicePrice));
        when(daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, WorkspaceEntityType.CUSTOMER))
                .thenReturn(Optional.of(customerPrice));
        when(daoBillingRecordService.saveBillingRecord(any(BillingRecord.class)))
                .thenReturn(new BillingRecord());

        //WHEN
        billingService.generateBillingRecord(workspaceId, timeInterval).get();

        //THEN
        verify(daoBillingRecordService).saveBillingRecord(argThat(record -> record != null
                && record.getWorkspaceId().equals(workspaceId)
                && record.getAssetCount().equals(entityCount)
                && record.getDeviceCount().equals(entityCount)
                && record.getCustomerCount().equals(entityCount)
                && record.getAssetPrice().equals(assetPrice.getPrice().multiply(BigDecimal.valueOf(entityCount)))
                && record.getDevicePrice().equals(devicePrice.getPrice().multiply(BigDecimal.valueOf(entityCount)))
                && record.getCustomerPrice().equals(customerPrice.getPrice().multiply(BigDecimal.valueOf(entityCount)))
                && record.getStartBillingPeriod().equals(timeInterval.startTime())
                && record.getEndBillingPeriod().equals(timeInterval.endTime())
        ));
    }
}