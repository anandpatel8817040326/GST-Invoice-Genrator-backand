package com.gstinvoice.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

public class NotificationDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendNotificationRequest {
        @NotBlank(message = "Recipient email is required")
        @Email
        private String recipientEmail;

        @NotBlank(message = "Subject is required")
        private String subject;

        @NotBlank(message = "Message body is required")
        private String messageBody;

        @NotBlank(message = "Template type is required")
        private String templateType; // REGISTRATION, PASSWORD_RESET, INVOICE_DELIVERY, PAYMENT_CONFIRMATION, PAYMENT_REMINDER
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationResponse {
        private Long id;
        private String recipientEmail;
        private String subject;
        private String messageBody;
        private String templateType;
        private String status;
        private LocalDateTime sentAt;
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

