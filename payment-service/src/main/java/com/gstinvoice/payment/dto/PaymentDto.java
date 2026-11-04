package com.gstinvoice.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecordPaymentRequest {
        @NotNull(message = "Invoice ID is required")
        private Long invoiceId;

        private String invoiceNumber;

        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
        private BigDecimal amount;

        private LocalDate paymentDate;

        @NotBlank(message = "Payment method is required (CASH, UPI, BANK_TRANSFER, CHEQUE, CARD)")
        private String paymentMethod;

        private String referenceNumber;
        private String notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private Long id;
        private Long businessId;
        private Long invoiceId;
        private String invoiceNumber;
        private String receiptNumber;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String paymentMethod;
        private String referenceNumber;
        private String notes;
        private String recordedBy;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
}

