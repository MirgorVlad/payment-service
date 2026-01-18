package org.mirgor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EntityType entityType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal price;


}
