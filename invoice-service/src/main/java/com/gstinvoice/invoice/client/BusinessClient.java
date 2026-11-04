package com.gstinvoice.invoice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "business-service")
public interface BusinessClient {

    @GetMapping("/api/businesses/{id}")
    ApiResponse<BusinessClientDto> getBusinessById(@PathVariable("id") Long id);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }

    @Data
    class BusinessClientDto {
        private Long id;
        private String businessName;
        private String gstin;
        private String pan;
        private String email;
        private String phone;
        private String addressLine1;
        private String city;
        private String state;
        private String stateCode;
        private String pincode;
        private String logoUrl;
        private String bankName;
        private String accountNumber;
        private String ifscCode;
        private String branchName;
        private String upiId;
        private String termsAndConditions;
        private String invoicePrefix;
    }
}

