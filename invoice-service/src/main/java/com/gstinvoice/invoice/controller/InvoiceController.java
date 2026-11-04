package com.gstinvoice.invoice.controller;

import com.gstinvoice.invoice.dto.InvoiceDto;
import com.gstinvoice.invoice.entity.InvoiceHistory;
import com.gstinvoice.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoice Processing", description = "Create invoices, statutory GST computation, payments, history, draft management")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @Operation(summary = "Create GST invoice with automatic tax calculations")
    public ResponseEntity<InvoiceDto.ApiResponse<InvoiceDto.InvoiceResponse>> createInvoice(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail,
            @Valid @RequestBody InvoiceDto.CreateInvoiceRequest request) {
        InvoiceDto.InvoiceResponse response = invoiceService.createInvoice(businessId, request, userEmail);
        return new ResponseEntity<>(
                InvoiceDto.ApiResponse.<InvoiceDto.InvoiceResponse>builder()
                        .success(true)
                        .message("Invoice generated successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceDto.ApiResponse<InvoiceDto.InvoiceResponse>> getInvoiceById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId) {
        InvoiceDto.InvoiceResponse response = invoiceService.getInvoiceById(id, businessId);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<InvoiceDto.InvoiceResponse>builder()
                        .success(true)
                        .message("Invoice retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "List invoices for business with optional search")
    public ResponseEntity<InvoiceDto.ApiResponse<List<InvoiceDto.InvoiceResponse>>> getInvoices(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestParam(value = "search", required = false) String search) {
        List<InvoiceDto.InvoiceResponse> list = invoiceService.getInvoicesByBusiness(businessId, search);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<List<InvoiceDto.InvoiceResponse>>builder()
                        .success(true)
                        .message("Invoices retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @PutMapping("/{id}/draft")
    @Operation(summary = "Update draft invoice")
    public ResponseEntity<InvoiceDto.ApiResponse<InvoiceDto.InvoiceResponse>> updateDraft(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail,
            @Valid @RequestBody InvoiceDto.CreateInvoiceRequest request) {
        InvoiceDto.InvoiceResponse response = invoiceService.updateDraftInvoice(id, businessId, request, userEmail);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<InvoiceDto.InvoiceResponse>builder()
                        .success(true)
                        .message("Draft invoice updated")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update invoice status (DRAFT, ISSUED, PAID, CANCELLED)")
    public ResponseEntity<InvoiceDto.ApiResponse<InvoiceDto.InvoiceResponse>> updateStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail,
            @RequestBody InvoiceDto.UpdateStatusRequest request) {
        InvoiceDto.InvoiceResponse response = invoiceService.updateInvoiceStatus(id, businessId, request.getStatus(), userEmail);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<InvoiceDto.InvoiceResponse>builder()
                        .success(true)
                        .message("Invoice status updated to " + request.getStatus())
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/payment")
    @Operation(summary = "Record invoice payment (partial or full)")
    public ResponseEntity<InvoiceDto.ApiResponse<InvoiceDto.InvoiceResponse>> recordPayment(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail,
            @Valid @RequestBody InvoiceDto.UpdatePaymentRequest request) {
        InvoiceDto.InvoiceResponse response = invoiceService.recordPayment(id, businessId, request, userEmail);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<InvoiceDto.InvoiceResponse>builder()
                        .success(true)
                        .message("Payment recorded successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}/draft")
    @Operation(summary = "Delete draft invoice")
    public ResponseEntity<InvoiceDto.ApiResponse<Void>> deleteDraft(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail) {
        invoiceService.deleteDraftInvoice(id, businessId, userEmail);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Draft invoice deleted")
                        .build()
        );
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get audit trail and status history for invoice")
    public ResponseEntity<InvoiceDto.ApiResponse<List<InvoiceHistory>>> getHistory(@PathVariable Long id) {
        List<InvoiceHistory> history = invoiceService.getInvoiceHistory(id);
        return ResponseEntity.ok(
                InvoiceDto.ApiResponse.<List<InvoiceHistory>>builder()
                        .success(true)
                        .message("Invoice history retrieved")
                        .data(history)
                        .build()
        );
    }
}

