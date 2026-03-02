package org.mirgor.service.mapper;

import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.UserDto;
import org.mirgor.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements EntityMapper<User, UserDto> {

    private final PasswordEncoder passwordEncoder;

    public User fromDto(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
