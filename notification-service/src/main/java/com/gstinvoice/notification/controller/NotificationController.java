package com.gstinvoice.notification.controller;

import com.gstinvoice.notification.dto.NotificationDto;
import com.gstinvoice.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification System", description = "Email notifications for invoice delivery, payment reminders, reset password")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification (Invoice, Receipt, Password Reset, Verification)")
    public ResponseEntity<NotificationDto.ApiResponse<NotificationDto.NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationDto.SendNotificationRequest request) {
        NotificationDto.NotificationResponse response = notificationService.sendNotification(request);
        return new ResponseEntity<>(
                NotificationDto.ApiResponse.<NotificationDto.NotificationResponse>builder()
                        .success(true)
                        .message("Notification queued and dispatched")
                        .data(response)
                        .build(),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping
    @Operation(summary = "Get recent notification logs")
    public ResponseEntity<NotificationDto.ApiResponse<List<NotificationDto.NotificationResponse>>> getRecent() {
        List<NotificationDto.NotificationResponse> list = notificationService.getRecentNotifications();
        return ResponseEntity.ok(
                NotificationDto.ApiResponse.<List<NotificationDto.NotificationResponse>>builder()
                        .success(true)
                        .message("Recent notifications retrieved")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/recipient")
    @Operation(summary = "Get notifications sent to a specific email")
    public ResponseEntity<NotificationDto.ApiResponse<List<NotificationDto.NotificationResponse>>> getByEmail(@RequestParam("email") String email) {
        List<NotificationDto.NotificationResponse> list = notificationService.getNotificationsByEmail(email);
        return ResponseEntity.ok(
                NotificationDto.ApiResponse.<List<NotificationDto.NotificationResponse>>builder()
                        .success(true)
                        .message("Notifications for recipient retrieved")
                        .data(list)
                        .build()
        );
    }
}

