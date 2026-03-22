package com.agrosupply.entity;

import com.agrosupply.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "farmer_requests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FarmerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The farmer who placed the request
    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    // Set by warehouse operator when stock is allocated
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = true)
    private Warehouse warehouse;

    // Set by warehouse operator when delivery is scheduled
    @ManyToOne
    @JoinColumn(name = "delivery_agent_id", nullable = true)
    private User deliveryAgent;

    // Status flow: PENDING → ALLOCATED → SCHEDULED → DELIVERED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @CreationTimestamp
    private LocalDateTime requestedAt;

    // Set when warehouse operator schedules the delivery
    private LocalDateTime scheduledAt;

    // One farmer request can have multiple products (line items)
    @OneToMany(mappedBy = "farmerRequest", cascade = CascadeType.ALL)
    private List<RequestItem> items;
}