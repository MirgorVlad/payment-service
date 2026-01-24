package org.mirgor.service.mapper;

import lombok.RequiredArgsConstructor;
import org.mirgor.dto.OperationDto;
import org.mirgor.entity.Operation;
import org.mirgor.service.WorkspaceService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationMapper implements EntityMapper<Operation, OperationDto> {

    private final WorkspaceService workspaceService;

    public Operation fromDto(OperationDto operationDto) {
        var workspaceOpt = workspaceService.getWorkspaceById(operationDto.getWorkspaceId());
        if (workspaceOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Workspace with if %s not fount", operationDto.getWorkspaceId()));
        }
        return Operation.builder()
                .workspace(workspaceOpt.get())
                .entityType(operationDto.getEntityType())
                .count(operationDto.getCount())
                .build();
    }

    public OperationDto toDto(Operation operation) {
        return OperationDto.builder()
                .id(operation.getId())
                .workspaceId(operation.getWorkspace().getId())
                .entityType(operation.getEntityType())
                .count(operation.getCount())
                .build();
    }
}
