package org.mirgor.service.billing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.BillingRecord;
import org.mirgor.common.dto.entity.Price;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.exception.BillingException;
import org.mirgor.service.billing.pricing_strategy.PricingStrategy;
import org.mirgor.service.dao.DaoBillingRecordService;
import org.mirgor.service.dao.DaoPriceService;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private DaoBillingRecordService daoBillingRecordService;
    @Mock
    private DaoWorkspaceService daoWorkspaceService;
    @Mock
    private DaoPriceService daoPriceService;
    @Mock
    private PricingStrategy pricingStrategy;

    private BillingService billingService;

    private Price assetPrice;
    private Price devicePrice;
    private Price customerPrice;
    private Long workspaceId;
    private Workspace workspace;

    @BeforeEach
    void init() {
        workspaceId = 1L;
        assetPrice = new Price(1L, workspaceId, WorkspaceEntityType.ASSET, BigDecimal.valueOf(7));
        devicePrice = new Price(2L, workspaceId, WorkspaceEntityType.DEVICE, BigDecimal.valueOf(8));
        customerPrice = new Price(3L, workspaceId, WorkspaceEntityType.CUSTOMER, BigDecimal.valueOf(9));
        workspace = Workspace.builder()
                .id(workspaceId)
                .currency(Currency.USD)
                .pricingStrategy(PricingStrategyType.MAX)
                .build();

        when(pricingStrategy.getPricingStrategyType()).thenReturn(PricingStrategyType.MAX);
        billingService = new BillingService(daoBillingRecordService, daoWorkspaceService, daoPriceService, List.of(pricingStrategy));
        ReflectionTestUtils.invokeMethod(billingService, "initStrategyMap");
    }

    @ParameterizedTest
    @MethodSource("provideEntityCountScenarios")
    @DisplayName("Should successfully generate billing record for workspace for selected period")
    public void shouldSuccessfullyCreateBillingRecord(long entityCount) throws ExecutionException, InterruptedException {
        //GIVEN
        var timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(daoWorkspaceService.findWorkspaceById(workspaceId)).thenReturn(Optional.of(workspace));
        when(pricingStrategy.calculate(eq(workspaceId), any(WorkspaceEntityType.class), eq(timeInterval)))
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

    @Test
    @DisplayName("Should generate billing record with 0 cost when no snapshots exist for the period")
    public void shouldSuccessfullyCreateBillingRecordIfNoSnapshotsFound() throws ExecutionException, InterruptedException {
        //GIVEN
        var timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(daoWorkspaceService.findWorkspaceById(workspaceId)).thenReturn(Optional.of(workspace));
        when(pricingStrategy.calculate(eq(workspaceId), any(WorkspaceEntityType.class), eq(timeInterval)))
                .thenReturn(0L);
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
                && record.getAssetCount().equals(0L)
                && record.getDeviceCount().equals(0L)
                && record.getCustomerCount().equals(0L)
                && record.getAssetPrice().compareTo(BigDecimal.ZERO) == 0
                && record.getDevicePrice().compareTo(BigDecimal.ZERO) == 0
                && record.getCustomerPrice().compareTo(BigDecimal.ZERO) == 0
                && record.getStartBillingPeriod().equals(timeInterval.startTime())
                && record.getEndBillingPeriod().equals(timeInterval.endTime())
        ));
    }

    @Test
    @DisplayName("Should throw BillingException if Price is not configured for any entity type")
    public void shouldFailIfPriceIsNotFoundForEntity() {
        //GIVEN
        var timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        var we = WorkspaceEntityType.values()[0];

        when(daoWorkspaceService.findWorkspaceById(workspaceId)).thenReturn(Optional.of(workspace));
        when(pricingStrategy.calculate(eq(workspaceId), any(WorkspaceEntityType.class), eq(timeInterval)))
                .thenReturn(0L);
        when(daoPriceService.findLatestByWorkspaceIdAndEntityType(workspaceId, we))
                .thenReturn(Optional.empty());

        //WHEN THEN
        var exception = assertThrows(
                BillingException.class,
                () -> billingService.generateBillingRecord(workspaceId, timeInterval)
        );

        Assertions.assertEquals(String.format("Price is not found for workspace %s and entity %s", workspaceId, we), exception.getMessage());
        verify(daoBillingRecordService, never()).saveBillingRecord(any(BillingRecord.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if Workspace is not found")
    public void shouldFailIfWorkspaceIsNotFound() {
        //GIVEN
        var timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(daoWorkspaceService.findWorkspaceById(workspaceId)).thenReturn(Optional.empty());

        //WHEN THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> billingService.generateBillingRecord(workspaceId, timeInterval)
        );

        verify(daoBillingRecordService, never()).saveBillingRecord(any(BillingRecord.class));
    }

    static Stream<Long> provideEntityCountScenarios() {
        return Stream.of(0L, 1L, 100L, Long.MAX_VALUE / 100);
    }
}
