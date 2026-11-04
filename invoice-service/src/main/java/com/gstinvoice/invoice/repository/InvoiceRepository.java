package com.gstinvoice.invoice.repository;

import com.gstinvoice.invoice.entity.Invoice;
import com.gstinvoice.invoice.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    Optional<Invoice> findByIdAndBusinessId(Long id, Long businessId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByBusinessIdAndStatus(Long businessId, InvoiceStatus status);
    List<Invoice> findByBusinessIdAndCustomerId(Long businessId, Long customerId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.businessId = :businessId")
    long countByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT i FROM Invoice i WHERE i.businessId = :businessId AND " +
           "(LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.buyerName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Invoice> searchInvoices(@Param("businessId") Long businessId, @Param("search") String search);
}

