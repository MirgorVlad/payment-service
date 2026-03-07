package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.entity.Workspace;
import org.mirgor.entity.WorkspaceEntity;
import org.mirgor.repository.WorkspaceRepository;
import org.mirgor.service.mapper.WorkspaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoWorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    @Transactional
    public Workspace saveWorkspace(Workspace workspace) {
        WorkspaceEntity entity;
        if (workspace.getId() != null) {
            entity = workspaceRepository.findById(workspace.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + workspace.getId()));
            entity.setEmail(workspace.getEmail());
            entity.setHost(workspace.getHost());
            entity.setPassword(workspace.getPassword());
            entity.setSyncStatus(workspace.getSyncStatus());
        } else {
            entity = workspaceMapper.fromDto(workspace);
        }
        return workspaceMapper.toDto(workspaceRepository.save(entity));
    }

    @Transactional
    public void deleteWorkspace(Long id) {
        workspaceRepository.deleteById(id);
    }

    public Optional<Workspace> findWorkspaceById(Long id) {
        return workspaceRepository.findById(id).map(workspaceMapper::toDto);
    }

    Optional<WorkspaceEntity> findWorkspaceEntityById(Long id) {
        return workspaceRepository.findById(id);
    }

    public List<Workspace> findAllWorkspaces() {
        return workspaceRepository.findAll().stream().map(workspaceMapper::toDto).toList();
    }

    public List<Workspace> findAllWorkspacesByUserId(Long userId) {
        return workspaceRepository.findByUserId(userId).stream().map(workspaceMapper::toDto).toList();
    }

    public boolean existsByIdAndUserId(Long id, Long userId) {
        return workspaceRepository.existsByIdAndUserId(id, userId);
    }
}
