package org.mirgor.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "snapshot")
public class SnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true)
    private UUID syncId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SnapshotEntityType snapshotEntityType;

    @Column(nullable = false)
    private Long count;

    @Column(nullable = false, updatable = false)
    private LocalDateTime snapshotTime;

    @PrePersist
    protected void onCreate() {
        snapshotTime = LocalDateTime.now();
    }

}
