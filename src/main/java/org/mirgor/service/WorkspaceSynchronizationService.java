package org.mirgor.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirgor.common.constant.OperationalEntityType;
import org.mirgor.common.constant.SyncStatus;
import org.mirgor.entity.Operation;
import org.mirgor.entity.Workspace;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.mirgor.service.rest.WorkspaceRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSynchronizationService {

    @Value("${executor.sync.threads_count:32}")
    private int SYNC_EXECUTOR_THREADS;

    private ExecutorService executorService;
    private final WorkspaceRestClient workspaceRestClient;
    private final DaoWorkspaceService daoWorkspaceService;
    private final OperationService operationService;

    @PostConstruct
    void init() {
        executorService = Executors.newFixedThreadPool(SYNC_EXECUTOR_THREADS);
    }

    @Scheduled(fixedRateString = "${workspace.sync.rate_millis: 60000}")
    public void syncAllWorkspaces() {
        var workspaceList = daoWorkspaceService.findAllWorkspaces();
        workspaceList.forEach(this::syncWorkspace);
    }

    @Async("dbTaskExecutor")
    @Scheduled(fixedRateString = "${workspace.sync.usage_snapshot: 3600000}")
    public CompletableFuture<List<Operation>> fetchWorkspacesUsageSnapshot() {
        log.debug("Start usage Snapshot");
        var startTime = System.currentTimeMillis();

        var workspaceList = daoWorkspaceService.findAllWorkspaces();
        List<CompletableFuture<List<Operation>>> operationSaveFutureList = workspaceList.stream().map(workspace -> {
            var countFuture = countWorkspaceEntities(workspace);
            return countFuture.thenApplyAsync(countMap ->
                    countMap.entrySet().stream()
                            .map(entry -> {
                                var operation = buildOperation(workspace, entry.getKey(), entry.getValue());
                                return operationService.createOperation(operation);
                            }).toList());
        }).toList();

        return CompletableFuture.allOf(operationSaveFutureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> operationSaveFutureList.stream()
                        .flatMap(f -> f.join().stream())
                        .toList())
                .whenComplete((result, ex) -> {
                    long duration = System.currentTimeMillis() - startTime;
                    if (ex != null) {
                        log.error("Usage snapshot failed after {}ms", duration, ex);
                    } else {
                        log.info("Usage snapshot completed in {}ms, saved {} operations", duration, result.size());
                    }
                });
    }

    private CompletableFuture<Map<OperationalEntityType, Long>> countWorkspaceEntities(Workspace workspace) {
        return workspaceRestClient.loginWorkspace(workspace)
                .thenCompose(token -> {
                    Map<OperationalEntityType, Long> entityCountMap = new ConcurrentHashMap<>();

                    List<CompletableFuture<Void>> futures = Arrays.stream(OperationalEntityType.values())
                            .map(entity -> workspaceRestClient.fetchAllEntitiesCount(workspace, token.token(), entity)
                                    .thenAccept(count -> entityCountMap.put(entity, count)))
                            .toList();

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> entityCountMap);
                });
    }

    private void syncWorkspace(Workspace workspace) {
        var pingFuture = workspaceRestClient.loginWorkspace(workspace);
        pingFuture.whenCompleteAsync((result, ex) -> {
            var status = ex == null ? SyncStatus.ACTIVE : SyncStatus.INACTIVE;
            workspace.setSyncStatus(status);
            daoWorkspaceService.saveWorkspace(workspace);

            log.info("Workspace [{}] PING: {}", workspace.getId(), status);
        }, executorService);
    }


    private static Operation buildOperation(Workspace workspace, OperationalEntityType entity, Long count) {
        return Operation.builder()
                .operationalEntityType(entity)
                .count(count)
                .workspace(workspace)
                .build();
    }
}
