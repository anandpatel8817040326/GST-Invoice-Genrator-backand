package com.gstinvoice.payment.controller;

import com.gstinvoice.payment.dto.PaymentDto;
import com.gstinvoice.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "Record payments, track receipts, payment methods (CASH, UPI, NEFT), outstanding balances")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Record a payment against an invoice")
    public ResponseEntity<PaymentDto.ApiResponse<PaymentDto.PaymentResponse>> recordPayment(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestHeader(value = "X-User-Email", required = false, defaultValue = "admin@example.com") String userEmail,
            @Valid @RequestBody PaymentDto.RecordPaymentRequest request) {
        PaymentDto.PaymentResponse response = paymentService.recordPayment(businessId, request, userEmail);
        return new ResponseEntity<>(
                PaymentDto.ApiResponse.<PaymentDto.PaymentResponse>builder()
                        .success(true)
                        .message("Payment recorded successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(summary = "List all payments for business")
    public ResponseEntity<PaymentDto.ApiResponse<List<PaymentDto.PaymentResponse>>> getPayments(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        List<PaymentDto.PaymentResponse> list = paymentService.getPaymentsByBusiness(businessId);
        return ResponseEntity.ok(
                PaymentDto.ApiResponse.<List<PaymentDto.PaymentResponse>>builder()
                        .success(true)
                        .message("Payments retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get payment history for a specific invoice")
    public ResponseEntity<PaymentDto.ApiResponse<List<PaymentDto.PaymentResponse>>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<PaymentDto.PaymentResponse> list = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(
                PaymentDto.ApiResponse.<List<PaymentDto.PaymentResponse>>builder()
                        .success(true)
                        .message("Invoice payments retrieved")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment details by ID")
    public ResponseEntity<PaymentDto.ApiResponse<PaymentDto.PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentDto.PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(
                PaymentDto.ApiResponse.<PaymentDto.PaymentResponse>builder()
                        .success(true)
                        .message("Payment retrieved successfully")
                        .data(response)
                        .build()
        );
    }
}

