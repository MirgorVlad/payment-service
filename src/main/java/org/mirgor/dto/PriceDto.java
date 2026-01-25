package org.mirgor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.entity.Currency;
import org.mirgor.entity.EntityType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {

    private Long id;

    @NotNull
    private Long workspaceId;

    @NotNull
    private EntityType entityType;

    @NotNull
    private Currency currency;

    @NotNull
    @Positive
    private BigDecimal price;
}
