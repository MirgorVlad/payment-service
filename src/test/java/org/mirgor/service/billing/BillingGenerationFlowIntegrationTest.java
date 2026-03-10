package org.mirgor.service.billing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.Role;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.Price;
import org.mirgor.common.dto.entity.Snapshot;
import org.mirgor.common.dto.entity.User;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.service.BaseIntegrationTest;
import org.mirgor.service.dao.DaoPriceService;
import org.mirgor.service.dao.DaoSnapshotService;
import org.mirgor.service.dao.DaoUserService;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class BillingGenerationFlowIntegrationTest extends BaseIntegrationTest {
    //TODO use WireMock

    @Autowired
    private BillingService billingService;
    @Autowired
    private DaoWorkspaceService daoWorkspaceService;
    @Autowired
    private DaoSnapshotService daoSnapshotService;
    @Autowired
    private DaoPriceService daoPriceService;
    @Autowired
    private DaoUserService daoUserService;


    @Test
    @DisplayName("Should successfully generate billing record with MAX payment strategy")
    void shouldSuccessfullyGenerateBillingRecord() throws ExecutionException, InterruptedException {
        //GIVEN
        TimeInterval timeInterval = new TimeInterval(LocalDateTime.now().minusHours(10), LocalDateTime.now());

        var user = createUser();
        var workspace = createWorkspace(user.getId(), "http://localhost:wirePort");
        var assetPrice = createPrice(workspace, WorkspaceEntityType.ASSET, 5);
        var devicePrice = createPrice(workspace, WorkspaceEntityType.DEVICE, 6);
        var customerPrice = createPrice(workspace, WorkspaceEntityType.CUSTOMER, 7);


        var assetSnapshot = createSnapshot(workspace, 100, WorkspaceEntityType.ASSET);
        var deviceSnapshot = createSnapshot(workspace, 120, WorkspaceEntityType.DEVICE);
        var customerSnapshot = createSnapshot(workspace, 130, WorkspaceEntityType.CUSTOMER);

        //WHEN
        var billingRecord = billingService.generateBillingRecord(workspace.getId(), timeInterval).get();

        //THEN
        assertNotNull(billingRecord);
        assertEquals(assetSnapshot.getCount(), billingRecord.getAssetCount());
        assertEquals(deviceSnapshot.getCount(), billingRecord.getDeviceCount());
        assertEquals(customerSnapshot.getCount(), billingRecord.getCustomerCount());
        assertEquals(assetPrice.getPrice().multiply(BigDecimal.valueOf(assetSnapshot.getCount())), billingRecord.getAssetPrice());
        assertEquals(devicePrice.getPrice().multiply(BigDecimal.valueOf(deviceSnapshot.getCount())), billingRecord.getDevicePrice());
        assertEquals(customerPrice.getPrice().multiply(BigDecimal.valueOf(customerSnapshot.getCount())), billingRecord.getCustomerPrice());
        assertEquals(workspace.getCurrency(), billingRecord.getCurrency());
    }

    // ---- Helpers -----

    private Snapshot createSnapshot(Workspace workspace, long count, WorkspaceEntityType entityType) {
        var snapshot = Snapshot.builder()
                .workspaceId(workspace.getId())
                .count(count)
                .workspaceEntityType(entityType)
                .snapshotTime(LocalDateTime.now().minusHours(1))
                .build();

        return daoSnapshotService.saveSnapshot(snapshot);
    }


    private Price createPrice(Workspace workspace, WorkspaceEntityType entityType, long price) {
        var priceEntity = Price.builder()
                .workspaceId(workspace.getId())
                .workspaceEntityType(entityType)
                .price(BigDecimal.valueOf(price))
                .build();
        return daoPriceService.savePrice(priceEntity);
    }

    private Workspace createWorkspace(Long userId, String host) {
        var workspace = Workspace.builder()
                .email("test@mail.com")
                .host(host)
                .password("password")
                .pricingStrategy(PricingStrategyType.MAX)
                .currency(Currency.USD)
                .userId(userId)
                .build();
        return daoWorkspaceService.saveWorkspace(workspace);
    }

    private User createUser() {
        var user = User.builder()
                .email("user@mail.com")
                .password("test")
                .role(Role.ADMIN)
                .build();
        return daoUserService.saveUser(user);
    }
}
