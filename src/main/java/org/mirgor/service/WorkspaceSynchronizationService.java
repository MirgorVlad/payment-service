package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.constant.SyncStatus;
import org.mirgor.common.dto.entity.Snapshot;
import org.mirgor.common.rest.WorkspaceLoginResponse;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.mirgor.service.rest.WorkspaceRestClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSynchronizationService {

    private final WorkspaceRestClient workspaceRestClient;
    private final DaoWorkspaceService daoWorkspaceService;
    private final SnapshotService snapshotService;
    private final Executor dbTaskExecutor;

    @Scheduled(fixedRateString = "${workspace.sync.rate_millis:60000}")
    void scheduleWorkspacesSync() {
        syncAllWorkspaces();
    }

    @Scheduled(fixedRateString = "${workspace.sync.usage_snapshot:3600000}")
    void scheduleWorkspaceSnapshot() {
        fetchWorkspacesUsageSnapshot();
    }

    @Async("dbTaskExecutor")
    public void syncAllWorkspaces() {
        var workspaceList = daoWorkspaceService.findAllWorkspaces();
        workspaceList.forEach(this::syncWorkspace);
    }

    @Async("dbTaskExecutor")
    public CompletableFuture<List<Snapshot>> fetchWorkspacesUsageSnapshot() {
        log.debug("Start usage Snapshot");
        var startTime = System.currentTimeMillis();

        var workspaceList = daoWorkspaceService.findAllWorkspaces();
        List<CompletableFuture<List<Snapshot>>> snapshotSaveFutureList = workspaceList.stream().map(workspace -> {
            var syncId = UUID.randomUUID();
            var countFuture = countWorkspaceEntities(workspace);
            return countFuture
                    .exceptionally(ex -> {
                        log.error("Failed to sync workspace [{}]: {}", workspace.getId(), ex.getMessage());
                        return Collections.emptyMap();
                    })
                    .thenApplyAsync(countMap ->
                            countMap.entrySet().stream()
                                    .map(entry -> {
                                        var snapshot = buildSnapshot(workspace, entry.getKey(), entry.getValue(), syncId);
                                        return snapshotService.createSnapshot(snapshot);
                                    }).toList(), dbTaskExecutor);
        }).toList();

        return CompletableFuture.allOf(snapshotSaveFutureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> snapshotSaveFutureList.stream()
                        .flatMap(f -> f.join().stream())
                        .toList())
                .whenComplete((result, ex) -> {
                    long duration = System.currentTimeMillis() - startTime;
                    if (ex != null) {
                        log.error("Usage snapshot failed after {}ms", duration, ex);
                    } else {
                        log.info("Usage snapshot completed in {}ms, saved {} snapshots", duration, result.size());
                    }
                });
    }

    private CompletableFuture<Map<WorkspaceEntityType, Long>> countWorkspaceEntities(Workspace workspace) {
        return workspaceRestClient.loginWorkspace(workspace)
                .thenCompose(token -> {
                    Map<WorkspaceEntityType, Long> entityCountMap = new ConcurrentHashMap<>();

                    List<CompletableFuture<Void>> futures = Arrays.stream(WorkspaceEntityType.values())
                            .map(entity -> workspaceRestClient.fetchAllEntitiesCount(workspace, token.token(), entity)
                                    .thenAccept(count -> entityCountMap.put(entity, count)))
                            .toList();

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> entityCountMap);
                });
    }

    private CompletableFuture<WorkspaceLoginResponse> syncWorkspace(Workspace workspace) {
        var pingFuture = workspaceRestClient.loginWorkspace(workspace);
        return pingFuture.whenCompleteAsync((result, ex) -> {
            var status = ex == null ? SyncStatus.ACTIVE : SyncStatus.INACTIVE;
            workspace.setSyncStatus(status);
            daoWorkspaceService.saveWorkspace(workspace);
            log.info("Workspace [{}] PING: {}", workspace.getId(), status);
        }, dbTaskExecutor);
    }

    private static Snapshot buildSnapshot(Workspace workspace, WorkspaceEntityType entity, Long count, UUID syncId) {
        return Snapshot.builder()
                .syncId(syncId)
                .workspaceEntityType(entity)
                .count(count)
                .workspaceId(workspace.getId())
                .build();
    }
}
