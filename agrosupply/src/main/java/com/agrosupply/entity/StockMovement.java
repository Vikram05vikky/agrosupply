package com.agrosupply.entity;

import com.agrosupply.enums.StockMovementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_warehouse_id", nullable = false)
    private Warehouse fromWarehouse;

    @ManyToOne
    @JoinColumn(name = "to_warehouse_id", nullable = false)
    private Warehouse toWarehouse;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Double supports decimal quantities
    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementStatus status;

    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime resolvedAt;
}