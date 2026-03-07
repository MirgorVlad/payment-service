package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.entity.User;
import org.mirgor.service.dao.DaoUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final DaoUserService daoUserService;

    public User createUser(User userDto) {
        userDto.setId(null);
        return daoUserService.saveUser(userDto);
    }

    public Optional<User> getUserById(Long id) {
        return daoUserService.findUserById(id);
    }

    public List<User> getAllUsers() {
        return daoUserService.findAllUsers();
    }

    public User updateUser(Long id, User updatedUser) {
        return daoUserService.updateUser(id, updatedUser);
    }

    public void deleteUser(Long id) {
        if (!daoUserService.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        daoUserService.deleteUser(id);
    }
}
