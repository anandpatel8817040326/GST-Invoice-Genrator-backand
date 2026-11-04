package com.gstinvoice.notification.service.impl;

import com.gstinvoice.notification.dto.NotificationDto;
import com.gstinvoice.notification.entity.NotificationLog;
import com.gstinvoice.notification.repository.NotificationRepository;
import com.gstinvoice.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationDto.NotificationResponse sendNotification(NotificationDto.SendNotificationRequest request) {
        log.info("Sending [{}] email to: {} with subject: {}", request.getTemplateType(), request.getRecipientEmail(), request.getSubject());

        NotificationLog logEntry = NotificationLog.builder()
                .recipientEmail(request.getRecipientEmail())
                .subject(request.getSubject())
                .messageBody(request.getMessageBody())
                .templateType(request.getTemplateType())
                .status("SENT")
                .build();

        NotificationLog saved = notificationRepository.save(logEntry);

        return NotificationDto.NotificationResponse.builder()
                .id(saved.getId())
                .recipientEmail(saved.getRecipientEmail())
                .subject(saved.getSubject())
                .messageBody(saved.getMessageBody())
                .templateType(saved.getTemplateType())
                .status(saved.getStatus())
                .sentAt(saved.getSentAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getRecentNotifications() {
        return notificationRepository.findTop50ByOrderBySentAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotificationsByEmail(String email) {
        return notificationRepository.findByRecipientEmailOrderBySentAtDesc(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationDto.NotificationResponse mapToResponse(NotificationLog n) {
        return NotificationDto.NotificationResponse.builder()
                .id(n.getId())
                .recipientEmail(n.getRecipientEmail())
                .subject(n.getSubject())
                .messageBody(n.getMessageBody())
                .templateType(n.getTemplateType())
                .status(n.getStatus())
                .sentAt(n.getSentAt())
                .build();
    }
}

