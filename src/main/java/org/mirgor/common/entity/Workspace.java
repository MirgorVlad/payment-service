package org.mirgor.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.SyncStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SyncStatus syncStatus;

    @JsonIgnore
    private Long userId;
}
