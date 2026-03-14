package org.mirgor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mirgor.common.constant.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing_record")
public class BillingRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceCount;

    private Long assetCount;

    private Long customerCount;

    private BigDecimal devicePrice;

    private BigDecimal assetPrice;

    private BigDecimal customerPrice;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspace;

    @Column(nullable = false)
    private LocalDateTime startBillingPeriod;

    @Column(nullable = false)
    private LocalDateTime endBillingPeriod;

}
