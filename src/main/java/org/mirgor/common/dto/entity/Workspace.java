package org.mirgor.common.dto.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mirgor.common.constant.Currency;
import org.mirgor.common.constant.PricingStrategyType;
import org.mirgor.common.constant.SyncStatus;

import java.time.LocalDateTime;

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

    @NotNull
    private Currency currency;

    @NotNull
    private PricingStrategyType pricingStrategy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastSyncTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SyncStatus syncStatus;

    @JsonIgnore
    private Long userId;

}
