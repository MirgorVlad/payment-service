package org.mirgor.repository;

import org.mirgor.entity.Operation;
import org.mirgor.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    boolean existsById(Long id);

    boolean existsByIdAndWorkspaceUserId(Long id, Long userId);

    List<Operation> findByWorkspaceUserId(Long userId);

    List<Operation> findByWorkspaceId(Long workspaceId);

    Optional<Operation> findByIdAndWorkspaceUserId(Long id, Long userId);
}
