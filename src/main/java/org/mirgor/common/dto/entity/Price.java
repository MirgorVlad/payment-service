package org.mirgor.common.dto.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.WorkspaceEntityType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    private Long id;

    @NotNull
    private Long workspaceId;

    @NotNull
    private WorkspaceEntityType workspaceEntityType;

    @NotNull
    private Currency currency; //TODO move to Workspace level

    @NotNull
    @Positive
    private BigDecimal price;
}
