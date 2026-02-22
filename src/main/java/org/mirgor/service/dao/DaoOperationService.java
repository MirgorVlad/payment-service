package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Operation;
import org.mirgor.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoOperationService {

    private final OperationRepository operationRepository;

    @Transactional
    public Operation saveOperation(Operation operation) {
        return operationRepository.save(operation);
    }

    @Transactional
    public void deleteOperation(Long id) {
        operationRepository.deleteById(id);
    }

    public Optional<Operation> findOperationById(Long id) {
        return operationRepository.findById(id);
    }

    public Optional<Operation> findByIdAndWorkspaceUserId(Long id, Long userId) {
        return operationRepository.findByIdAndWorkspaceUserId(id, userId);
    }

    public boolean existsByIdAndWorkspaceUserId(Long id, Long userId) {
        return operationRepository.existsByIdAndWorkspaceUserId(id, userId);
    }

    public List<Operation> findByWorkspaceId(Long workspaceId) {
        return operationRepository.findByWorkspaceId(workspaceId);
    }

    public List<Operation> findByWorkspaceUserId(Long userId) {
        return operationRepository.findByWorkspaceUserId(userId);
    }
}
