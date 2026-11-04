package com.gstinvoice.report.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/api/payments")
    ApiResponse<List<PaymentSummaryDto>> getPayments(
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId
    );

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class PaymentSummaryDto {
        private Long id;
        private Long businessId;
        private Long invoiceId;
        private String invoiceNumber;
        private String receiptNumber;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String paymentMethod;
    }
}

