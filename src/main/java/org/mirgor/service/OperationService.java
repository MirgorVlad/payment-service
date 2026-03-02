package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirgor.entity.Operation;
import org.mirgor.security.utils.SecurityUtil;
import org.mirgor.service.dao.DaoOperationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationService {

    private final DaoOperationService daoOperationService;

    public Operation createOperation(Operation operation) {
        operation.setId(null);
        log.debug("Save operation {}", operation);
        return daoOperationService.saveOperation(operation);
    }

    public Operation updateOperation(Long id, Operation updatedOperation) {
        var operation = getOperationById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Operation with id %s is not found", id)));

        operation.setWorkspace(updatedOperation.getWorkspace());
        operation.setOperationalEntityType(updatedOperation.getOperationalEntityType());
        operation.setCount(updatedOperation.getCount());

        return daoOperationService.saveOperation(operation);
    }

    public void deleteOperation(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        if (!daoOperationService.existsByIdAndWorkspaceUserId(id, userId)) {
            throw new IllegalArgumentException(String.format("Operation with id %s is not found", id));
        }
        daoOperationService.deleteOperation(id);
    }

    public Optional<Operation> getOperationById(Long id) {
        var userId = SecurityUtil.getCurrentUserId();
        return daoOperationService.findByIdAndWorkspaceUserId(id, userId);
    }

    public List<Operation> getAllOperations(Long workspaceId) {
        var userId = SecurityUtil.getCurrentUserId();
        if (workspaceId != null) {
            return daoOperationService.findByWorkspaceId(workspaceId);
        }
        return daoOperationService.findByWorkspaceUserId(userId);
    }
}
