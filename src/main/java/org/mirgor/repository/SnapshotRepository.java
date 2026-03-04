package org.mirgor.repository;

import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.entity.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SnapshotRepository extends JpaRepository<SnapshotEntity, Long> {

    @Query("""
            SELECT MAX(s.count) FROM SnapshotEntity s
            WHERE s.workspace = :workspaceId
              AND s.workspaceEntityType = :entityType
              AND s.snapshotTime >= :startTime
              AND s.snapshotTime < :endTime
            """)
    Long findMaxEntityCountByWorkspaceAndPeriod(
            @Param("workspaceId") long workspaceId,
            @Param("entityType") WorkspaceEntityType workspaceEntityType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    boolean existsById(Long id);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);

    List<SnapshotEntity> findBySyncId(UUID syncId);

    List<SnapshotEntity> findByWorkspaceUserId(Long userId);

    List<SnapshotEntity> findByWorkspaceId(Long workspaceId);

    Optional<SnapshotEntity> findByIdAndWorkspaceUserId(Long id, Long userId);
}
