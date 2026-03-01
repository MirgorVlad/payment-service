package org.mirgor.service.mapper;

import org.mirgor.common.dto.workspace.Workspace;
import org.mirgor.entity.UserEntity;
import org.mirgor.entity.WorkspaceEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper implements EntityMapper<WorkspaceEntity, Workspace> {

    public WorkspaceEntity fromDto(Workspace workspace) {
        return WorkspaceEntity.builder()
                .id(workspace.getId())
                .email(workspace.getEmail())
                .host(workspace.getHost())
                .password(workspace.getPassword())
                .user(workspace.getUserId() != null ? new UserEntity(workspace.getUserId()) : null)
                .build();
    }

    public Workspace toDto(WorkspaceEntity workspaceEntity) {
        return Workspace.builder()
                .id(workspaceEntity.getId())
                .email(workspaceEntity.getEmail())
                .host(workspaceEntity.getHost())
                .password(workspaceEntity.getPassword())
                .syncStatus(workspaceEntity.getSyncStatus())
                .userId(workspaceEntity.getUser() != null ? workspaceEntity.getUser().getId() : null)
                .build();
    }
}
