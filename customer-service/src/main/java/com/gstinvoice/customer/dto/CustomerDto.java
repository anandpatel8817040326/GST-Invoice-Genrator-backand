package com.gstinvoice.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

public class CustomerDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerRequest {
        @NotBlank(message = "Customer name is required")
        private String customerName;

        private String email;
        private String mobile;

        @Pattern(regexp = "^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GSTIN format")
        private String gstin;

        @Pattern(regexp = "^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
        private String pan;

        private String addressLine1;
        private String addressLine2;
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "State code is required (e.g. 27, 29)")
        @Pattern(regexp = "^[0-9]{2}$", message = "State code must be 2 digits")
        private String stateCode;

        private String pinCode;
        private String customerType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerResponse {
        private Long id;
        private Long businessId;
        private String customerName;
        private String email;
        private String mobile;
        private String gstin;
        private String pan;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String stateCode;
        private String pinCode;
        private String customerType;
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

