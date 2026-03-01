package org.mirgor.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.SnapshotEntityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot {

    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID syncId;

    @NotNull
    private Long workspaceId;

    @NotNull
    private SnapshotEntityType snapshotEntityType;

    @NotNull
    @Min(1)
    private Long count;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime snapshotTime;
}
