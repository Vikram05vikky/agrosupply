package com.agrosupply.entity;

import com.agrosupply.enums.RequestItemStatus;

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
@Table(name = "request_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_request_id", nullable = false)
    private FarmerRequest farmerRequest;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Double supports decimal quantities e.g. 50.5 kg, 2.5 litres
    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestItemStatus status;
}