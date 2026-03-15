package org.mirgor.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mirgor.common.constant.*;
import org.mirgor.common.dto.entity.Snapshot;
import org.mirgor.common.dto.entity.User;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.service.dao.DaoUserService;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceSynchronizationIntegrationTest extends BaseIntegrationTest {


    public static final String X_AUTHORIZATION = "X-Authorization";

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private WorkspaceSynchronizationService syncService;
    @Autowired
    private DaoWorkspaceService daoWorkspaceService;
    @Autowired
    private DaoUserService daoUserService;

    private Workspace workspace;


    @BeforeEach
    void setUp() {
        User user = daoUserService.saveUser(User.builder()
                .email(UUID.randomUUID() + "@test.com")
                .password("secret")
                .role(Role.USER)
                .build());

        workspace = daoWorkspaceService.saveWorkspace(Workspace.builder()
                .email("tb@test.com")
                .host(wireMock.baseUrl())
                .password("pass")
                .pricingStrategy(PricingStrategyType.MAX)
                .currency(Currency.USD)
                .userId(user.getId())
                .build());
    }

    @AfterEach
    void tearDown() {
        var userId = workspace.getUserId();
        daoWorkspaceService.deleteWorkspace(workspace.getId());
        daoUserService.deleteUser(userId);
        wireMock.resetAll();
    }

    @Test
    @DisplayName("fetchWorkspacesUsageSnapshot saves one Snapshot per entity type")
    void shouldSaveSnapshotForEachEntityType() throws ExecutionException, InterruptedException {

        wireMock.stubFor(post(urlPathEqualTo("/api/auth/login"))
                .willReturn(okJson("""
                        {"token": "test-jwt-token", "refreshToken": "test-refresh"}
                        """)));


        wireMock.stubFor(get(urlPathEqualTo("/api/assetInfos/all"))
                .withHeader(X_AUTHORIZATION, equalTo("Bearer test-jwt-token"))
                .willReturn(okJson("""
                        {"data": [], "totalPages": 1, "totalElements": 42, "hasNext": false}
                        """)));

        wireMock.stubFor(get(urlPathEqualTo("/api/deviceInfos/all"))
                .withHeader(X_AUTHORIZATION, equalTo("Bearer test-jwt-token"))
                .willReturn(okJson("""
                        {"data": [], "totalPages": 1, "totalElements": 85, "hasNext": false}
                        """)));

        wireMock.stubFor(get(urlPathEqualTo("/api/customerInfos/all"))
                .withHeader(X_AUTHORIZATION, equalTo("Bearer test-jwt-token"))
                .willReturn(okJson("""
                        {"data": [], "totalPages": 1, "totalElements": 17, "hasNext": false}
                        """)));


        List<Snapshot> all = syncService.fetchWorkspacesUsageSnapshot().get();

        List<Snapshot> snapshots = all.stream()
                .filter(s -> s.getWorkspaceId().equals(workspace.getId()))
                .toList();

        assertEquals(3, snapshots.size(), "One snapshot per entity type");

        assertEquals(42L, findByType(snapshots, WorkspaceEntityType.ASSET).getCount());
        assertEquals(85L, findByType(snapshots, WorkspaceEntityType.DEVICE).getCount());
        assertEquals(17L, findByType(snapshots, WorkspaceEntityType.CUSTOMER).getCount());


        wireMock.verify(postRequestedFor(urlPathEqualTo("/api/auth/login")));
        wireMock.verify(getRequestedFor(urlPathEqualTo("/api/assetInfos/all")));
        wireMock.verify(getRequestedFor(urlPathEqualTo("/api/deviceInfos/all")));
        wireMock.verify(getRequestedFor(urlPathEqualTo("/api/customerInfos/all")));
    }

    @Test
    @DisplayName("syncAllWorkspaces marks workspace ACTIVE when login succeeds")
    void shouldMarkWorkspaceActiveOnSuccessfulPing() throws InterruptedException {
        wireMock.stubFor(post(urlPathEqualTo("/api/auth/login"))
                .willReturn(okJson("""
                        {"token": "test-jwt-token", "refreshToken": "test-refresh"}
                        """)));

        syncService.syncAllWorkspaces();

        await().atMost(1, SECONDS).until(() ->
                daoWorkspaceService.findWorkspaceById(workspace.getId())
                        .map(w -> w.getSyncStatus() == SyncStatus.ACTIVE)
                        .orElse(false));

        Workspace updated = daoWorkspaceService.findWorkspaceById(workspace.getId()).orElseThrow();
        assertEquals(SyncStatus.ACTIVE, updated.getSyncStatus());
    }

    @Test
    @DisplayName("syncAllWorkspaces marks workspace INACTIVE when login returns 500")
    void shouldMarkWorkspaceInactiveOnFailedPing() throws InterruptedException {

        wireMock.stubFor(post(urlPathEqualTo("/api/auth/login"))
                .willReturn(serverError()));

        syncService.syncAllWorkspaces();

        await().atMost(10, SECONDS).until(() ->
                daoWorkspaceService.findWorkspaceById(workspace.getId())
                        .map(w -> w.getSyncStatus() == SyncStatus.INACTIVE)
                        .orElse(false));

        Workspace updated = daoWorkspaceService.findWorkspaceById(workspace.getId()).orElseThrow();
        assertEquals(SyncStatus.INACTIVE, updated.getSyncStatus());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Snapshot findByType(List<Snapshot> snapshots, WorkspaceEntityType type) {
        return snapshots.stream()
                .filter(s -> s.getWorkspaceEntityType() == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No snapshot found for type: " + type));
    }
}
