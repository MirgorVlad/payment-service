package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirgor.common.entity.SyncStatus;
import org.mirgor.common.entity.WorkspaceConstants;
import org.mirgor.dto.workspace.WorkspacePingRequest;
import org.mirgor.entity.Workspace;
import org.mirgor.exception.WorkspaceAvailabilityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSynchronizationService {

    @Value("${executor.sync.threads_count:32}")
    private int SYNC_EXECUTOR_THREADS;

    private final WebClient webClient;
    private final WorkspaceService workspaceService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(SYNC_EXECUTOR_THREADS);

    public void syncWorkspace(Workspace workspace){
        var pingFuture = pingWorkspace(workspace);
        pingFuture.whenCompleteAsync((result, ex) -> {
            var status = ex == null ? SyncStatus.ACTIVE : SyncStatus.INACTIVE;
            workspace.setSyncStatus(status);
            workspaceService.updateWorkspace(workspace.getId(), workspace); //TODO handle service role check
        }, executorService);
    }

    //TODO check JPA entity
    private CompletableFuture<?> pingWorkspace(Workspace workspace) {
        return webClient.post()
                .uri(String.format(WorkspaceConstants.WORKSPACE_PING_URL_PATTERN, workspace.getHost()))
                .bodyValue(new WorkspacePingRequest(workspace.getEmail(), workspace.getPassword()))
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, resp ->
                        Mono.error(new WorkspaceAvailabilityException("Workspace ping failed")))
                .bodyToMono(Object.class)
                .toFuture();
    }
}
