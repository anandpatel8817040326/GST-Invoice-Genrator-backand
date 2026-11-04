package com.gstinvoice.notification.service;

import com.gstinvoice.notification.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto.NotificationResponse sendNotification(NotificationDto.SendNotificationRequest request);
    List<NotificationDto.NotificationResponse> getRecentNotifications();
    List<NotificationDto.NotificationResponse> getNotificationsByEmail(String email);
}

