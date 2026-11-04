package com.gstinvoice.invoice.repository;

import com.gstinvoice.invoice.entity.InvoiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceHistoryRepository extends JpaRepository<InvoiceHistory, Long> {
    List<InvoiceHistory> findByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);
}

