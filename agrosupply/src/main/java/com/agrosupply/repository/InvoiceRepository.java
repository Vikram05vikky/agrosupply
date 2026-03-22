package com.agrosupply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.Invoice;
import com.agrosupply.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Get all invoices for a specific farmer
    List<Invoice> findByFarmerId(Long farmerId);

    // Get all invoices by payment status
    List<Invoice> findByStatus(InvoiceStatus status);

    // Get invoice by request — one request = one invoice
    Optional<Invoice> findByFarmerRequestId(Long requestId);
}   