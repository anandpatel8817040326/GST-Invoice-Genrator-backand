package com.gstinvoice.report.repository;

import com.gstinvoice.report.entity.ReportSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {
    List<ReportSnapshot> findByBusinessIdAndReportTypeOrderByCreatedAtDesc(Long businessId, String reportType);
}

