package org.mirgor.service.mapper;

import org.mirgor.common.dto.Snapshot;
import org.mirgor.entity.SnapshotEntity;
import org.springframework.stereotype.Component;

@Component
public class SnapshotMapper {

    public Snapshot toDto(SnapshotEntity snapshotEntity) {
        return Snapshot.builder()
                .id(snapshotEntity.getId())
                .workspaceId(snapshotEntity.getWorkspace().getId())
                .snapshotEntityType(snapshotEntity.getSnapshotEntityType())
                .count(snapshotEntity.getCount())
                .snapshotTime(snapshotEntity.getSnapshotTime())
                .build();
    }
}
