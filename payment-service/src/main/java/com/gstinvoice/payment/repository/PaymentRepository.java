package com.gstinvoice.payment.repository;

import com.gstinvoice.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBusinessIdOrderByPaymentDateDesc(Long businessId);
    List<Payment> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId);
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    long countByBusinessId(Long businessId);
}

