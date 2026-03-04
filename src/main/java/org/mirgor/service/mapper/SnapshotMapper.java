package org.mirgor.service.mapper;

import org.mirgor.common.entity.Snapshot;
import org.mirgor.entity.SnapshotEntity;
import org.springframework.stereotype.Component;

@Component
public class SnapshotMapper implements EntityMapper<SnapshotEntity, Snapshot>{

    @Override
    public SnapshotEntity fromDto(Snapshot entityDto) {
        return SnapshotEntity.builder()
                .workspaceEntityType(entityDto.getWorkspaceEntityType())
                .count(entityDto.getCount())
                .build();
    }

    public Snapshot toDto(SnapshotEntity snapshotEntity) {
        return Snapshot.builder()
                .id(snapshotEntity.getId())
                .syncId(snapshotEntity.getSyncId())
                .workspaceId(snapshotEntity.getWorkspace().getId())
                .workspaceEntityType(snapshotEntity.getWorkspaceEntityType())
                .count(snapshotEntity.getCount())
                .snapshotTime(snapshotEntity.getSnapshotTime())
                .build();
    }
}
