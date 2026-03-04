package org.mirgor.common.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRecord {

    private Long id;
    @NotNull
    private Long workspaceId;
    @Min(0)
    private Long deviceCount;
    @Min(0)
    private Long assetCount;
    @Min(0)
    private Long customerCount;
    @Min(0)
    private BigDecimal devicePrice;
    @Min(0)
    private BigDecimal assetPrice;
    @Min(0)
    private BigDecimal customerPrice;

    @NotNull
    private LocalDateTime startBillingPeriod;

    @NotNull
    private LocalDateTime endBillingPeriod;
}
