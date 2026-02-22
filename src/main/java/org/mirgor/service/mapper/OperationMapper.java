package org.mirgor.service.mapper;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.OperationDto;
import org.mirgor.entity.Operation;
import org.mirgor.service.WorkspaceService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationMapper implements EntityMapper<Operation, OperationDto> {

    private final WorkspaceService workspaceService;

    public Operation fromDto(OperationDto operationDto) {
        var workspace = workspaceService.getWorkspaceById(operationDto.getWorkspaceId());
        if (workspace == null) {
            throw new IllegalArgumentException(String.format("Workspace with if %s not found", operationDto.getWorkspaceId()));
        }
        return Operation.builder()
                .workspace(workspace)
                .operationalEntityType(operationDto.getOperationalEntityType())
                .count(operationDto.getCount())
                .build();
    }

    public OperationDto toDto(Operation operation) {
        return OperationDto.builder()
                .id(operation.getId())
                .workspaceId(operation.getWorkspace().getId())
                .operationalEntityType(operation.getOperationalEntityType())
                .count(operation.getCount())
                .build();
    }
}
