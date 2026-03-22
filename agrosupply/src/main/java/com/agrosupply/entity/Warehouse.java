package com.agrosupply.entity;

import com.agrosupply.enums.WarehouseStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    // Separate capacity limits per unit type
    // Each unit type is tracked independently — never mixed together
    private Double maxCapacityKg;       // for seeds, fertilizers (e.g. 500000.0 kg)
    private Double maxCapacityLitre;    // for pesticides, liquids (e.g. 10000.0 litres)
    private Integer maxCapacityPiece;   // for equipment          (e.g. 5000 pieces)

    // Warehouse operator assigned to manage this warehouse
    // nullable — warehouse can exist before an operator is assigned
    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = true)
    private User operator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseStatus status;
}