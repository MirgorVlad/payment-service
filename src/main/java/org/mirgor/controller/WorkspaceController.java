package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.dto.workspace.WorkspaceDto;
import org.mirgor.service.WorkspaceService;
import org.mirgor.service.mapper.WorkspaceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper mapper;

    @PostMapping
    public ResponseEntity<WorkspaceDto> createWorkspace(@RequestBody @Valid WorkspaceDto workspaceDto) {
        try {
            var workspace = mapper.fromDto(workspaceDto);
            var createdWorkspace = workspaceService.createWorkspace(workspace);
            return new ResponseEntity<>(mapper.toDto(createdWorkspace), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceDto>> getAllWorkspaces() {
        var workspaces = workspaceService.getAllWorkspaces();
        var workspaceDtos = workspaces.stream()
                .map(mapper::toDto)
                .toList();
        return new ResponseEntity<>(workspaceDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDto> getWorkspaceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mapper.toDto(workspaceService.getWorkspaceById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDto> updateWorkspace(@PathVariable Long id, @RequestBody @Valid WorkspaceDto workspaceDto) {
        try {
            var updatedWorkspace = workspaceService.updateWorkspace(id, mapper.fromDto(workspaceDto));
            var updatedWorkspaceDto = mapper.toDto(updatedWorkspace);
            return ResponseEntity.ok(updatedWorkspaceDto);
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
