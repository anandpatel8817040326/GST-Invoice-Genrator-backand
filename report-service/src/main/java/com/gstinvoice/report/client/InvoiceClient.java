package com.gstinvoice.report.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "invoice-service")
public interface InvoiceClient {

    @GetMapping("/api/invoices")
    ApiResponse<List<InvoiceSummaryDto>> getInvoices(
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId
    );

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class InvoiceSummaryDto {
        private Long id;
        private Long businessId;
        private Long customerId;
        private String invoiceNumber;
        private LocalDate invoiceDate;
        private String buyerName;
        private String buyerGstin;
        private String buyerState;
        private String buyerStateCode;
        private String placeOfSupply;
        private BigDecimal taxableSubtotal;
        private BigDecimal totalDiscount;
        private BigDecimal totalCgst;
        private BigDecimal totalSgst;
        private BigDecimal totalIgst;
        private BigDecimal totalTax;
        private BigDecimal grandTotal;
        private BigDecimal paidAmount;
        private BigDecimal balanceDue;
        private String status;
        private String paymentStatus;
        private List<InvoiceItemDto> items;
    }

    @Data
    class InvoiceItemDto {
        private Long productId;
        private String productName;
        private String hsnSac;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal taxableAmount;
        private BigDecimal gstRate;
        private BigDecimal totalTax;
        private BigDecimal totalAmount;
    }
}

