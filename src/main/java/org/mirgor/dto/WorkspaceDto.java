package org.mirgor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDto {

    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String host;

    @NotBlank
    @Min(6)
    @Max(100)
    private String password;

    @JsonIgnore
    private Long userId;
}
