package org.mirgor.repository;

import org.mirgor.entity.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SnapshotRepository extends JpaRepository<SnapshotEntity, Long> {

    boolean existsById(Long id);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);

    List<SnapshotEntity> findBySyncId(UUID syncId);

    List<SnapshotEntity> findByWorkspaceUserId(Long userId);

    List<SnapshotEntity> findByWorkspaceId(Long workspaceId);

    Optional<SnapshotEntity> findByIdAndWorkspaceUserId(Long id, Long userId);
}
