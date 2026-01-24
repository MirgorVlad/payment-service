package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Operation;
import org.mirgor.repository.OperationRepository;
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
        if (operationRepository.existsById(operation.getId())) {
            throw new RuntimeException("Operation ID already exists: " + operation.getId());
        }

        return operationRepository.save(operation);
    }

    public Optional<Operation> getOperationById(Long id) {
        return operationRepository.findById(id);
    }

    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    @Transactional
    public Operation updateOperation(Long id, Operation updatedOperation) {
        Operation existingOperation = operationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found with id: " + id));

        if (updatedOperation.getWorkspace() != null) {
            existingOperation.setWorkspace(updatedOperation.getWorkspace());
        }
        if (updatedOperation.getEntityType() != null) {
            existingOperation.setEntityType(updatedOperation.getEntityType());
        }
        if (updatedOperation.getCount() != null) {
            existingOperation.setCount(updatedOperation.getCount());
        }

        return operationRepository.save(existingOperation);
    }

    @Transactional
    public void deleteOperation(Long id) {
        if (!operationRepository.existsById(id)) {
            throw new RuntimeException("Operation not found with id: " + id);
        }
        operationRepository.deleteById(id);
    }
}
