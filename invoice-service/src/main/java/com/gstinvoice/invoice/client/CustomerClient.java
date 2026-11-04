package com.gstinvoice.invoice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    ApiResponse<CustomerClientDto> getCustomerById(@PathVariable("id") Long id);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class CustomerClientDto {
        private Long id;
        private Long businessId;
        private String customerName;
        private String email;
        private String mobile;
        private String gstin;
        private String pan;
        private String addressLine1;
        private String city;
        private String state;
        private String stateCode;
        private String pinCode;
        private String customerType;
    }
}

