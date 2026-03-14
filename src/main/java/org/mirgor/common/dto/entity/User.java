package org.mirgor.common.dto.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.Role;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
