package org.mirgor.service;

import lombok.RequiredArgsConstructor;
import org.mirgor.entity.User;
import org.mirgor.service.dao.DaoUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final DaoUserService daoUserService;

    public User createUser(User user) {
        user.setId(null);
        return daoUserService.saveUser(user);
    }

    public Optional<User> getUserById(Long id) {
        return daoUserService.findUserById(id);
    }

    public List<User> getAllUsers() {
        return daoUserService.findAllUsers();
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = daoUserService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        return daoUserService.saveUser(existingUser);
    }

    public void deleteUser(Long id) {
        if (!daoUserService.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        daoUserService.deleteUser(id);
    }
}
