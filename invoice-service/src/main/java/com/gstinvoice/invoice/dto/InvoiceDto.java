package com.gstinvoice.invoice.dto;

import com.gstinvoice.invoice.entity.InvoiceStatus;
import com.gstinvoice.invoice.entity.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceItemRequest {
        private Long productId;
        private String productName;
        private String description;
        private String hsnSac;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        private String unit;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
        private BigDecimal unitPrice;

        @Builder.Default
        private BigDecimal discount = BigDecimal.ZERO;

        @NotNull(message = "GST rate is required")
        @DecimalMin(value = "0.0", message = "GST rate must be non-negative")
        private BigDecimal gstRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateInvoiceRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        private String customInvoiceNumber;
        private LocalDate invoiceDate;
        private LocalDate dueDate;
        private String placeOfSupply;
        private Boolean reverseCharge;

        @NotEmpty(message = "Invoice must contain at least one item")
        @Valid
        private List<InvoiceItemRequest> items;

        @Builder.Default
        private BigDecimal overallDiscount = BigDecimal.ZERO;

        private String notes;
        private String termsAndConditions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String description;
        private String hsnSac;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal taxableAmount;
        private BigDecimal gstRate;
        private BigDecimal cgstRate;
        private BigDecimal cgstAmount;
        private BigDecimal sgstRate;
        private BigDecimal sgstAmount;
        private BigDecimal igstRate;
        private BigDecimal igstAmount;
        private BigDecimal totalTax;
        private BigDecimal totalAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceResponse {
        private Long id;
        private Long businessId;
        private Long customerId;
        private String invoiceNumber;
        private LocalDate invoiceDate;
        private LocalDate dueDate;
        private String sellerName;
        private String sellerGstin;
        private String sellerAddress;
        private String sellerState;
        private String sellerStateCode;
        private String buyerName;
        private String buyerGstin;
        private String buyerAddress;
        private String buyerState;
        private String buyerStateCode;
        private String placeOfSupply;
        private Boolean reverseCharge;
        private BigDecimal taxableSubtotal;
        private BigDecimal totalDiscount;
        private BigDecimal totalCgst;
        private BigDecimal totalSgst;
        private BigDecimal totalIgst;
        private BigDecimal totalTax;
        private BigDecimal roundOff;
        private BigDecimal grandTotal;
        private BigDecimal paidAmount;
        private BigDecimal balanceDue;
        private String amountInWords;
        private String notes;
        private String termsAndConditions;
        private InvoiceStatus status;
        private PaymentStatus paymentStatus;
        private List<InvoiceItemResponse> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        private InvoiceStatus status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePaymentRequest {
        private BigDecimal paidAmount;
        private String remarks;
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

