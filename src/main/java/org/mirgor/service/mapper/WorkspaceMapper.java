package org.mirgor.service.mapper;

import org.mirgor.dto.WorkspaceDto;
import org.mirgor.entity.Workspace;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper implements EntityMapper<Workspace, WorkspaceDto> {

    public Workspace fromDto(WorkspaceDto workspaceDto) {
        return Workspace.builder()
                .id(workspaceDto.getId())
                .email(workspaceDto.getEmail())
                .password(workspaceDto.getPassword()) //TODO fix when Spring Security Added
                .build();
    }

    public WorkspaceDto toDto(Workspace workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .email(workspace.getEmail())
                .build();
    }
}
