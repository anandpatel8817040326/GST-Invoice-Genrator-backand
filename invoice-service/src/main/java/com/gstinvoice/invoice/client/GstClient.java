package com.gstinvoice.invoice.client;

import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "gst-service")
public interface GstClient {

    @PostMapping("/api/gst/calculate")
    ApiResponse<GstCalculationResponse> calculate(@RequestBody GstCalculationRequest request);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    @Builder
    class GstCalculationRequest {
        private String sellerStateCode;
        private String buyerStateCode;
        private List<GstCalculationItem> items;
        private BigDecimal overallDiscount;
    }

    @Data
    @Builder
    class GstCalculationItem {
        private Long productId;
        private String productName;
        private String hsnSac;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal gstRate;
    }

    @Data
    class GstCalculationResponse {
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

    @Data
    class CalculatedItemResponse {
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
}

