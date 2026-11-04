package com.gstinvoice.notification.repository;

import com.gstinvoice.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientEmailOrderBySentAtDesc(String recipientEmail);
    List<NotificationLog> findTop50ByOrderBySentAtDesc();
}

