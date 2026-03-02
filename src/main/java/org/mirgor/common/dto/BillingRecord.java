package org.mirgor.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRecord {

    private Long id;

    @NotNull
    private Long workspaceId;

    private Long deviceCount;

    private Long assetCount;

    private Long customerCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime billingTime;
}
