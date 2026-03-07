package org.mirgor.service.rest;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.constant.WorkspaceConstants;
import org.mirgor.common.rest.EntityRequestContext;
import org.mirgor.common.rest.PageResponse;
import org.mirgor.common.rest.WorkspaceLoginResponse;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.common.dto.workspace.WorkspacePingRequest;
import org.mirgor.exception.WorkspaceAvailabilityException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class WorkspaceRestClient {

    private static final int PAGE_SIZE = 100;
    public static final int MAX_ATTEMPTS = 3;
    public static final int INITIAL_DELAY = 1;
    public static final String X_AUTHORIZATION = "X-Authorization";

    private final WebClient webClient;

    public CompletableFuture<WorkspaceLoginResponse> loginWorkspace(Workspace workspace) {
        return webClient.post()
                .uri(String.format(WorkspaceConstants.WORKSPACE_PING_URL_PATTERN, workspace.getHost()))
                .bodyValue(new WorkspacePingRequest(workspace.getEmail(), workspace.getPassword()))
                .retrieve()
                .onStatus(r -> !r.is2xxSuccessful(), resp ->
                        Mono.error(new WorkspaceAvailabilityException(String.format("Workspace [%s] ping failed", workspace.getId()))))
                .bodyToMono(WorkspaceLoginResponse.class)
                .retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(INITIAL_DELAY)))
                .toFuture();
    }

    public CompletableFuture<Long> fetchAllEntitiesCount(Workspace workspace, String token, WorkspaceEntityType entityType) {
        var path = WorkspaceConstants.fetchEndpointMap.get(entityType);
        if (path == null) {
            throw new IllegalArgumentException(String.format("Unsupported entity type [%s]", entityType));
        }
        var context = new EntityRequestContext(workspace.getHost(), path, token);
        return fetchPage(0, context)
                .map(PageResponse::totalElements)
                .toFuture();
    }

    private Mono<Integer> fetchAllPages(int page, EntityRequestContext context, AtomicInteger count) {
        return fetchPage(page, context)
                .flatMap(response -> {
                    count.addAndGet(response.data().size());
                    if (response.hasNext()) {
                        return fetchAllPages(page + 1, context, count);
                    } else {
                        return Mono.just(count.get());
                    }
                });
    }

    private Mono<PageResponse<?>> fetchPage(int page, EntityRequestContext context) {
        return webClient.get()
                .uri(context.host() + context.path() + "?page=" + page + "&pageSize=" + PAGE_SIZE + "&includeCustomers=true")
                .header(X_AUTHORIZATION, "Bearer " + context.token())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<?>>() {
                })
                .retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(INITIAL_DELAY)));
    }
}
