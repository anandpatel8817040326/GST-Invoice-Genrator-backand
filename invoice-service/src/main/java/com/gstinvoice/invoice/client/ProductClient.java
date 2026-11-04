package com.gstinvoice.invoice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<ProductClientDto> getProductById(@PathVariable("id") Long id);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class ProductClientDto {
        private Long id;
        private Long businessId;
        private String productName;
        private String description;
        private String hsnSac;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal gstRate;
        private String category;
    }
}

