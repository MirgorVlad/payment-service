package org.mirgor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mirgor.common.constant.SyncStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspaces",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_email_host",
                        columnNames = {"email", "host"}
                )
        })
public class WorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String host;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    private LocalDateTime lastSyncTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_payment_config")
    private WorkspacePaymentConfigEntity workspacePaymentConfig;  //TODO why LAZY failed?

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<SnapshotEntity> snapshot;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<PriceEntity> priceList;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<BillingRecordEntity> billingRecordEntityList;

}

