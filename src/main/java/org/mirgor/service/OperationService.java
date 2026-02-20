package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Operation;
import org.mirgor.repository.OperationRepository;
import org.mirgor.security.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;

    @Transactional
    public Operation createOperation(Operation operation) {
        operation.setId(null);
        return operationRepository.save(operation);
    }

    @Transactional
    public Operation updateOperation(Long id, Operation updatedOperation) {
        var operationOptional = getOperationById(id);
        var operation = operationOptional.orElseThrow(() ->
                new IllegalArgumentException(String.format("Operation with id %s is not found", id)));


        operation.setWorkspace(updatedOperation.getWorkspace());
        operation.setOperationalEntityType(updatedOperation.getOperationalEntityType());
        operation.setCount(updatedOperation.getCount());

        return operationRepository.save(operation);
    }

    @Transactional
    public void deleteOperation(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!operationRepository.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new RuntimeException("Operation not found with id: " + id);
        }
        operationRepository.deleteById(id);
    }

    public Optional<Operation> getOperationById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return operationRepository.findByIdAndWorkspaceUserId(id, userId);
    }

    public List<Operation> getAllOperations(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return operationRepository.findByWorkspaceId(workspaceId);
        }
        return operationRepository.findByWorkspaceUserId(userId);
    }
}
