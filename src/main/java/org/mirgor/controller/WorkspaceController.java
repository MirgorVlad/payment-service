package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.Snapshot;
import org.mirgor.common.dto.workspace.Workspace;
import org.mirgor.service.WorkspaceService;
import org.mirgor.service.WorkspaceSynchronizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceSynchronizationService workspaceSynchronizationService;
    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<Workspace> createWorkspace(@RequestBody @Valid Workspace workspaceDto) {
        try {
            var createdWorkspace = workspaceService.createWorkspace(workspaceDto);
            return new ResponseEntity<>(createdWorkspace, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Workspace> syncWorkspaces() {
        workspaceSynchronizationService.syncAllWorkspaces();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/snapshot")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Snapshot> createWorkspacesSnapshot() throws ExecutionException, InterruptedException {
        return workspaceSynchronizationService.fetchWorkspacesUsageSnapshot().get(); //TODO ASYNC
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        var workspaces = workspaceService.getAllWorkspaces();
        return new ResponseEntity<>(workspaces, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(workspaceService.getWorkspaceById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workspace> updateWorkspace(@PathVariable Long id, @RequestBody @Valid Workspace workspace) {
        try {
            var updatedWorkspace = workspaceService.updateWorkspace(id, workspace);
            return ResponseEntity.ok(updatedWorkspace);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        try {
            workspaceService.deleteWorkspace(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
