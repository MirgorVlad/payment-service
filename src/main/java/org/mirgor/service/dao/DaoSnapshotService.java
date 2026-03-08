package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.Snapshot;
import org.mirgor.entity.SnapshotEntity;
import org.mirgor.repository.SnapshotRepository;
import org.mirgor.service.mapper.SnapshotMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DaoSnapshotService {

    private final SnapshotRepository snapshotRepository;
    private final DaoWorkspaceService daoWorkspaceService;
    private final SnapshotMapper snapshotMapper;

    @Transactional
    public Snapshot saveSnapshot(Snapshot snapshot) {
        var workspaceEntity = daoWorkspaceService.findWorkspaceEntityById(snapshot.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + snapshot.getWorkspaceId()));
        SnapshotEntity entity;
        if (snapshot.getId() != null) {
            entity = snapshotRepository.findById(snapshot.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Snapshot not found: " + snapshot.getId()));
            entity.setWorkspace(workspaceEntity);
            entity.setWorkspaceEntityType(snapshot.getWorkspaceEntityType());
            entity.setCount(snapshot.getCount());
            entity.setSyncId(UUID.randomUUID());
        } else {
            entity = snapshotMapper.fromDto(snapshot);
            entity.setWorkspace(workspaceEntity);
        }
        return snapshotMapper.toDto(snapshotRepository.save(entity));
    }

    @Transactional
    public void deleteSnapshot(Long id) {
        snapshotRepository.deleteById(id);
    }

    public List<Snapshot> findBySyncId(UUID syncId) {
        return snapshotRepository.findBySyncId(syncId).stream()
                .map(snapshotMapper::toDto)
                .toList();
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

    public Optional<Long> findMaxEntityCountByWorkspaceAndPeriod(Long workspaceId, WorkspaceEntityType type, TimeInterval timeInterval) {
        return snapshotRepository.findMaxEntityCountByWorkspaceAndPeriod(workspaceId, type, timeInterval.startTime(), timeInterval.endTime());
    }
}
