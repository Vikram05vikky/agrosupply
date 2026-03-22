package com.agrosupply.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.agrosupply.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupplierOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // The procurement officer who placed this order
    @ManyToOne
    @JoinColumn(name = "procurement_officer_id", nullable = false)
    private User procurementOfficer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // PENDING, DELIVERED, CANCELLED

    @CreationTimestamp
    private LocalDateTime orderedAt;

    // Set when procurement officer marks the order as delivered
    private LocalDateTime deliveredAt;

    // One supplier order can have multiple products (line items)
    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}