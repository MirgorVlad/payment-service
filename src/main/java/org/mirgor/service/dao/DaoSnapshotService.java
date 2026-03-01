package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.Snapshot;
import org.mirgor.entity.SnapshotEntity;
import org.mirgor.repository.SnapshotRepository;
import org.mirgor.service.mapper.SnapshotMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoSnapshotService {

    private final SnapshotRepository snapshotRepository;
    private final DaoWorkspaceService daoWorkspaceService;
    private final SnapshotMapper snapshotMapper;

    @Transactional
    public Snapshot saveSnapshot(Snapshot dto) {
        var workspaceEntity = daoWorkspaceService.findWorkspaceEntityById(dto.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + dto.getWorkspaceId()));
        SnapshotEntity entity;
        if (dto.getId() != null) {
            entity = snapshotRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Snapshot not found: " + dto.getId()));
            entity.setWorkspace(workspaceEntity);
            entity.setSnapshotEntityType(dto.getSnapshotEntityType());
            entity.setCount(dto.getCount());
        } else {
            entity = SnapshotEntity.builder()
                    .workspace(workspaceEntity)
                    .snapshotEntityType(dto.getSnapshotEntityType())
                    .count(dto.getCount())
                    .build();
        }
        return snapshotMapper.toDto(snapshotRepository.save(entity));
    }

    @Transactional
    public void deleteSnapshot(Long id) {
        snapshotRepository.deleteById(id);
    }

    public Optional<Snapshot> findSnapshotById(Long id) {
        return snapshotRepository.findById(id).map(snapshotMapper::toDto);
    }

    public Optional<Snapshot> findByIdAndWorkspaceUserId(Long id, Long userId) {
        return snapshotRepository.findByIdAndWorkspaceUserId(id, userId).map(snapshotMapper::toDto);
    }

    public boolean existsByIdAndWorkspaceUserId(Long id, Long userId) {
        return snapshotRepository.existsByIdAndWorkspaceUserId(id, userId);
    }

    public List<Snapshot> findByWorkspaceId(Long workspaceId) {
        return snapshotRepository.findByWorkspaceId(workspaceId).stream().map(snapshotMapper::toDto).toList();
    }

    public List<Snapshot> findByWorkspaceUserId(Long userId) {
        return snapshotRepository.findByWorkspaceUserId(userId).stream().map(snapshotMapper::toDto).toList();
    }
}
