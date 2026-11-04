package com.gstinvoice.payment.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "invoice-service")
public interface InvoiceClient {

    @PostMapping("/api/invoices/{id}/payment")
    ApiResponse<InvoiceResponseDto> recordPayment(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestBody PaymentUpdateDto request
    );

    @GetMapping("/api/invoices/{id}")
    ApiResponse<InvoiceResponseDto> getInvoiceById(@PathVariable("id") Long id);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class PaymentUpdateDto {
        private BigDecimal paidAmount;
        private String remarks;
    }

    @Data
    class InvoiceResponseDto {
        private Long id;
        private Long businessId;
        private String invoiceNumber;
        private BigDecimal grandTotal;
        private BigDecimal paidAmount;
        private BigDecimal balanceDue;
        private String paymentStatus;
    }
}

