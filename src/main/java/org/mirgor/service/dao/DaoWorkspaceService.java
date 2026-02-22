package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.Workspace;
import org.mirgor.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoWorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Workspace saveWorkspace(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long id) {
        workspaceRepository.deleteById(id);
    }

    public Optional<Workspace> findWorkspaceById(Long id) {
        return workspaceRepository.findById(id);
    }

    public List<Workspace> findAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    public List<Workspace> findAllWorkspacesByUserId(Long userId) {
        return workspaceRepository.findByUserId(userId);
    }

    public boolean existsByIdAndUserId(Long id, Long userId) {
        return workspaceRepository.existsByIdAndUserId(id, userId);
    }
}
