package com.agrosupply.controller;

import com.agrosupply.dto.request.PayRequest;
import com.agrosupply.dto.response.InvoiceResponse;
import com.agrosupply.enums.InvoiceStatus;
import com.agrosupply.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // GET /api/invoices — Admin views all invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET /api/invoices/{id} — Get invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // GET /api/invoices/farmer/{farmerId} — Farmer views their invoices
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByFarmer(@PathVariable Long farmerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByFarmer(farmerId));
    }

    // GET /api/invoices/filter?status=UNPAID — Filter by status
    @GetMapping("/filter")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(
            @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    /*
     * PATCH /api/invoices/{id}/pay
     * Farmer marks invoice as paid
     * Body: { "paymentMethod": "CASH" }
     * Updates: status → PAID, paidAt → NOW(), paymentMethod → set
     */
    @PatchMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable Long id,
                                                       @Valid @RequestBody PayRequest request) {
        return ResponseEntity.ok(invoiceService.payInvoice(id, request));
    }
}