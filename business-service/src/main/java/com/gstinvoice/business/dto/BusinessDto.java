package com.gstinvoice.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

public class BusinessDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateBusinessRequest {
        @NotBlank(message = "Business name is required")
        private String businessName;

        @Pattern(regexp = "^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GSTIN format (must be 15 alphanumeric characters)")
        private String gstin;

        @Pattern(regexp = "^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
        private String pan;

        private String email;
        private String phone;
        private String website;
        private String addressLine1;
        private String addressLine2;
        private String city;

        @NotBlank(message = "State is required for Indian GST compliance")
        private String state;

        @NotBlank(message = "2-digit State code is required (e.g., 27 for Maharashtra, 29 for Karnataka)")
        @Pattern(regexp = "^[0-9]{2}$", message = "State code must be 2 digits")
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessResponse {
        private Long id;
        private Long ownerUserId;
        private String businessName;
        private String gstin;
        private String pan;
        private String email;
        private String phone;
        private String website;
        private String addressLine1;
        private String addressLine2;
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
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Role is required")
        private String role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberResponse {
        private Long id;
        private Long businessId;
        private Long userId;
        private String name;
        private String email;
        private String role;
        private LocalDateTime joinedAt;
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

