package org.mirgor.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspace;

    @Column(nullable = false)
    private LocalDateTime startBillingPeriod;

    @Column(nullable = false)
    private LocalDateTime endBillingPeriod;

}
