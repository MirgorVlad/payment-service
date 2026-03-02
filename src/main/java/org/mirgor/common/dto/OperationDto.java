package org.mirgor.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.OperationalEntityType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {

    private Long id;

    @NotNull
    private Long workspaceId;

    @NotNull
    private OperationalEntityType operationalEntityType;

    @NotNull
    @Min(1)
    private Long count;
}
