package org.mirgor.service.dao;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.User;
import org.mirgor.entity.UserEntity;
import org.mirgor.exception.ResourceAlreadyExistsException;
import org.mirgor.repository.UserRepository;
import org.mirgor.service.mapper.UserMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User saveUser(User userDto) {
        UserEntity entity = userMapper.fromDto(userDto);
        try {
            return userMapper.toDto(userRepository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceAlreadyExistsException(entity, "email", entity.getEmail());
        }
    }

    @Transactional
    public User updateUser(Long id, User userDto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserEntity updates = userMapper.fromDto(userDto);
        if (updates.getEmail() != null) {
            entity.setEmail(updates.getEmail());
        }
        if (updates.getPassword() != null) {
            entity.setPassword(updates.getPassword());
        }
        return userMapper.toDto(userRepository.save(entity));
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
