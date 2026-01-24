package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Workspace;
import org.mirgor.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Workspace createWorkspace(Workspace workspace) {
        if (workspace.getId() != null && workspaceRepository.existsById(workspace.getId())) {
            throw new RuntimeException("Workspace ID already exists: " + workspace.getId());
        }

        return workspaceRepository.save(workspace);
    }

    public Optional<Workspace> getWorkspaceById(Long id) {
        return workspaceRepository.findById(id);
    }

    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    @Transactional
    public Workspace updateWorkspace(Long id, Workspace updatedWorkspace) {
        Workspace existingWorkspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

        if (updatedWorkspace.getEmail() != null) {
            existingWorkspace.setEmail(updatedWorkspace.getEmail());
        }
        if (updatedWorkspace.getId() != null && !updatedWorkspace.getId().equals(existingWorkspace.getId())) {
            if (workspaceRepository.existsById(updatedWorkspace.getId())) {
                throw new RuntimeException("Workspace ID already exists: " + updatedWorkspace.getId());
            }
            existingWorkspace.setId(updatedWorkspace.getId());
        }

        return workspaceRepository.save(existingWorkspace);
    }

    @Transactional
    public void deleteWorkspace(Long id) {
        if (!workspaceRepository.existsById(id)) {
            throw new RuntimeException("Workspace not found with id: " + id);
        }
        workspaceRepository.deleteById(id);
    }
}
