package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirgor.common.dto.entity.Snapshot;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.dao.DaoSnapshotService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final DaoSnapshotService daoSnapshotService;

    public Snapshot createSnapshot(Snapshot snapshot) {
        snapshot.setId(null);
        log.debug("Save snapshot {}", snapshot);
        return daoSnapshotService.saveSnapshot(snapshot);
    }

    public Snapshot updateSnapshot(Long id, Snapshot updatedSnapshot) {
        var existing = getSnapshotById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Snapshot with id %s is not found", id)));

        log.debug("Update snapshot {} with id {}", updatedSnapshot, id);
        existing.setWorkspaceId(updatedSnapshot.getWorkspaceId());
        existing.setWorkspaceEntityType(updatedSnapshot.getWorkspaceEntityType());
        existing.setCount(updatedSnapshot.getCount());

        return daoSnapshotService.saveSnapshot(existing);
    }

    public void deleteSnapshot(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!daoSnapshotService.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new IllegalArgumentException(String.format("Snapshot with id %s is not found", id));
        }
        log.debug("Delete snapshot with id {}", id);
        daoSnapshotService.deleteSnapshot(id);
    }

    public Optional<Snapshot> getSnapshotById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return daoSnapshotService.findByIdAndWorkspaceUserId(id, userId);
    }

    public List<Snapshot> getAllSnapshots(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return daoSnapshotService.findByWorkspaceId(workspaceId);
        }
        return daoSnapshotService.findByWorkspaceUserId(userId);
    }

}
