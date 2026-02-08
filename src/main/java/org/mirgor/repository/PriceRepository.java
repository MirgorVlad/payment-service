package org.mirgor.repository;

import org.mirgor.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    boolean existsById(Long id);

    Optional<Price> findByIdAndWorkspaceUserId(Long id, Long userId);

    List<Price> findByWorkspaceId(Long workspaceId);

    List<Price> findByWorkspaceUserId(Long userId);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);
}
