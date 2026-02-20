package org.mirgor.service.mapper;

import org.mirgor.dto.workspace.WorkspaceDto;
import org.mirgor.entity.Workspace;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper implements EntityMapper<Workspace, WorkspaceDto> {

    public Workspace fromDto(WorkspaceDto workspaceDto) {
        return Workspace.builder()
                .email(workspaceDto.getEmail())
                .host(workspaceDto.getHost())
                .password(workspaceDto.getPassword())
                .build();
    }

    public WorkspaceDto toDto(Workspace workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .email(workspace.getEmail())
                .host(workspace.getHost())
                .password(workspace.getPassword())
                .build();
    }
}
