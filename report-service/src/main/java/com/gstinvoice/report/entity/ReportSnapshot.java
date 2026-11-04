package com.gstinvoice.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // DASHBOARD, GSTR1, CUSTOMER, PRODUCT

    @Column(name = "period_key", length = 30)
    private String periodKey; // e.g. "2024-10"

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "total_tax", precision = 12, scale = 2)
    private BigDecimal totalTax;

    @Column(name = "invoice_count")
    private Integer invoiceCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

