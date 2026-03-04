package org.mirgor.repository;

import org.mirgor.common.constant.WorkspaceEntityType;
import org.mirgor.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Long> {

    @Query("""
            SELECT p FROM PriceEntity p
            WHERE p.workspace = :workspaceId
             AND p.workspaceEntityType = :entityType
             ORDER BY p.createTime DESC
             LIMIT 1
            """)
    BigDecimal findLatestByWorkspaceIdAndEntityType(@Param("workspaceId") Long workspaceId,
                                                    @Param("entityType") WorkspaceEntityType entityType);

    boolean existsById(Long id);

    Optional<PriceEntity> findByIdAndWorkspaceUserId(Long id, Long userId);

    List<PriceEntity> findByWorkspaceId(Long workspaceId);

    List<PriceEntity> findByWorkspaceUserId(Long userId);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);
}
