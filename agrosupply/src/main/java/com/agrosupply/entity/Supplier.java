package com.agrosupply.entity;

import com.agrosupply.enums.SupplierStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Stores contact details as a single string (e.g., "9876543210 | supplier@mail.com")
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status;
}