package com.agrosupply.entity;

import com.agrosupply.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One delivery maps to exactly one farmer request
    @OneToOne
    @JoinColumn(name = "request_id", nullable = false)
    private FarmerRequest farmerRequest;

    // Set when delivery agent marks it as completed
    private LocalDateTime deliveredAt;

    // IN_PROGRESS → COMPLETED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;
}