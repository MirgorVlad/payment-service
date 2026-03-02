package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.Role;
import org.mirgor.entity.User;
import org.mirgor.entity.Workspace;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.dao.DaoWorkspaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final DaoWorkspaceService daoWorkspaceService;
    private final WorkspaceSynchronizationService workspaceSynchronizationService;

    public Workspace createWorkspace(Workspace workspace) {
        var userId = SecurityUtil.getCurrentUserId();
        workspace.setId(null);
        workspace.setUser(new User(userId));
        return daoWorkspaceService.saveWorkspace(workspace);
    }

    public Workspace updateWorkspace(Long id, Workspace updatedWorkspace) {
        var userId = SecurityUtil.getCurrentUserId();
        var workspaceOptional = daoWorkspaceService.findWorkspaceById(id);
        var workspace = workspaceOptional.orElse(null);

        if (workspace == null || !workspace.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(String.format("Workspace with id %s is not found", id));
        }

        workspace.setEmail(updatedWorkspace.getEmail());
        workspace.setPassword(updatedWorkspace.getPassword());
        workspace.setHost(updatedWorkspace.getHost());
        workspace.setSyncStatus(updatedWorkspace.getSyncStatus());

        return daoWorkspaceService.saveWorkspace(workspace);
    }

    public void deleteWorkspace(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!daoWorkspaceService.existsByIdAndUserId(id, userId)) {
            throw new IllegalArgumentException("Workspace not found with id: " + id);
        }
        daoWorkspaceService.deleteWorkspace(id);
    }

    public Workspace getWorkspaceById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        var workspace = daoWorkspaceService.findWorkspaceById(id).orElse(null);

        if (workspace != null) {
            if (SecurityUtil.getCurrentUserRole().equals(Role.ADMIN)) {
                return workspace;
            } else if (workspace.getUser().getId().equals(userId)) {
                return workspace;
            }
        }
        throw new IllegalArgumentException("Workspace not found with id: " + id);
    }

    public List<Workspace> getAllWorkspaces() {
        var userId = SecurityUtil.getCurrentUserId();
        if (SecurityUtil.getCurrentUserRole().equals(Role.ADMIN)) {
            return daoWorkspaceService.findAllWorkspaces();
        }
        return daoWorkspaceService.findAllWorkspacesByUserId(userId);
    }


}
