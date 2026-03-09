package org.mirgor.service.mapper;

import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.entity.UserEntity;
import org.mirgor.entity.WorkspaceEntity;
import org.mirgor.entity.WorkspacePaymentConfigEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper implements EntityMapper<WorkspaceEntity, Workspace> {

    public WorkspaceEntity fromDto(Workspace workspace) {
        var workspacePaymentConfig = new WorkspacePaymentConfigEntity(null, workspace.getCurrency(), workspace.getPricingStrategy());
        return WorkspaceEntity.builder()
                .id(workspace.getId())
                .email(workspace.getEmail())
                .host(workspace.getHost())
                .password(workspace.getPassword())
                .user(workspace.getUserId() != null ? new UserEntity(workspace.getUserId()) : null)
                .workspacePaymentConfig(workspacePaymentConfig)
                .build();
    }

    public Workspace toDto(WorkspaceEntity workspaceEntity) {
        var workspacePaymentConfig = workspaceEntity.getWorkspacePaymentConfig();
        return Workspace.builder()
                .id(workspaceEntity.getId())
                .email(workspaceEntity.getEmail())
                .host(workspaceEntity.getHost())
                .currency(workspacePaymentConfig.getCurrency())
                .pricingStrategy(workspacePaymentConfig.getPricingStrategy())
                .lastSyncTime(workspaceEntity.getLastSyncTime())
                .password(workspaceEntity.getPassword())
                .syncStatus(workspaceEntity.getSyncStatus())
                .userId(workspaceEntity.getUser() != null ? workspaceEntity.getUser().getId() : null)
                .build();
    }
}
