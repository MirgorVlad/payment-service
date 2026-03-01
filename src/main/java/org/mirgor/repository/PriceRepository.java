package org.mirgor.repository;

import org.mirgor.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Long> {

    boolean existsById(Long id);

    Optional<PriceEntity> findByIdAndWorkspaceUserId(Long id, Long userId);

    List<PriceEntity> findByWorkspaceId(Long workspaceId);

    List<PriceEntity> findByWorkspaceUserId(Long userId);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);
}
