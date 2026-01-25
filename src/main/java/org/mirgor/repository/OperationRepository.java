package org.mirgor.repository;

import org.mirgor.entity.Operation;
import org.mirgor.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    boolean existsById(Long id);
}
