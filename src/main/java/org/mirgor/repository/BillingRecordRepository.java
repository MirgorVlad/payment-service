package org.mirgor.repository;

import org.mirgor.entity.BillingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, Long> {

    List<BillingRecordEntity> findByWorkspaceId(Long workspaceId);

    List<BillingRecordEntity> findByWorkspaceUserId(Long userId);

    Optional<BillingRecordEntity> findByIdAndWorkspaceUserId(Long id, Long userId);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);
}
