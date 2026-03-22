package com.agrosupply.entity;

import com.agrosupply.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The farmer who needs to pay
    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    // One invoice maps to exactly one farmer request
    @OneToOne
    @JoinColumn(name = "request_id", nullable = false)
    private FarmerRequest farmerRequest;

    // Auto-calculated: sum of (quantity × price) for each request item
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Auto-set when delivery is completed
    @CreationTimestamp
    private LocalDateTime issuedAt;

    // Set when farmer marks payment as done
    private LocalDateTime paidAt;

    // e.g. CASH, BANK_TRANSFER, MOBILE_MONEY — nullable until paid
    private String paymentMethod;

    // UNPAID → PAID
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
}