package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.User;
import org.mirgor.entity.Workspace;
import org.mirgor.repository.WorkspaceRepository;
import org.mirgor.security.config.SecurityConfig;
import org.mirgor.security.principal.SecurityUser;
import org.mirgor.security.utils.SecurityUtil;
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
        var userId = SecurityUtil.getCurrentUserId();
        workspace.setId(null);
        workspace.setUser(new User(userId));
        return workspaceRepository.save(workspace);
    }

    public Optional<Workspace> getWorkspaceById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return workspaceRepository.findByIdAndUserId(id, userId);
    }

    public List<Workspace> getAllWorkspaces() {
        var userId = SecurityUtil.getCurrentUserId();
        return workspaceRepository.findByUserId(userId);
    }

    @Transactional
    public Workspace updateWorkspace(Long id, Workspace updatedWorkspace) {
        var workspaceOptional = getWorkspaceById(id);
        var workspace = workspaceOptional.orElseThrow(() ->
                new IllegalArgumentException(String.format("Workspace with id %s is not found", id)));

        workspace.setEmail(updatedWorkspace.getEmail());
        workspace.setPassword(updatedWorkspace.getPassword());
        workspace.setHost(updatedWorkspace.getHost());

        return workspaceRepository.save(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!workspaceRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Workspace not found with id: " + id);
        }
        workspaceRepository.deleteById(id);
    }
}
