package com.gstinvoice.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductRequest {
        @NotBlank(message = "Product name is required")
        private String productName;

        private String description;
        private String hsnSac;

        private String unit;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
        private BigDecimal unitPrice;

        @NotNull(message = "GST rate is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "GST rate must be non-negative")
        private BigDecimal gstRate;

        private String category;
        private Integer stockQuantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductResponse {
        private Long id;
        private Long businessId;
        private String productName;
        private String description;
        private String hsnSac;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal gstRate;
        private String category;
        private Integer stockQuantity;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
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

