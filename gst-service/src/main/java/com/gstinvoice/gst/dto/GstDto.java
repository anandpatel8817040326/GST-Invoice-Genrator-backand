package com.gstinvoice.gst.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class GstDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalculationItemRequest {
        private Long productId;
        private String productName;
        private String hsnSac;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
        private BigDecimal quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
        private BigDecimal unitPrice;

        @Builder.Default
        private BigDecimal discount = BigDecimal.ZERO;

        @NotNull(message = "GST rate is required")
        @DecimalMin(value = "0.00", message = "GST rate cannot be negative")
        private BigDecimal gstRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalculationRequest {
        @NotBlank(message = "Seller State Code is required")
        private String sellerStateCode;

        @NotBlank(message = "Place of Supply / Buyer State Code is required")
        private String buyerStateCode;

        @NotEmpty(message = "At least one item is required for tax calculation")
        @Valid
        private List<CalculationItemRequest> items;

        @Builder.Default
        private BigDecimal overallDiscount = BigDecimal.ZERO;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalculatedItemResponse {
        private Long productId;
        private String productName;
        private String hsnSac;
        private BigDecimal quantity;
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
    public static class CalculationResponse {
        private boolean isIntraState;
        private String sellerStateCode;
        private String buyerStateCode;
        private List<CalculatedItemResponse> items;
        private BigDecimal taxableSubtotal;
        private BigDecimal totalDiscount;
        private BigDecimal totalCgst;
        private BigDecimal totalSgst;
        private BigDecimal totalIgst;
        private BigDecimal totalTax;
        private BigDecimal roundOff;
        private BigDecimal grandTotal;
        private String amountInWords;
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

