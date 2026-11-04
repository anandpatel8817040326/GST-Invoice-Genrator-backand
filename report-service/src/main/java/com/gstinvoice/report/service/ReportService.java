package com.gstinvoice.report.service;

import com.gstinvoice.report.dto.ReportDto;

import java.util.List;

public interface ReportService {
    ReportDto.DashboardMetricsResponse getDashboardMetrics(Long businessId);
    ReportDto.Gstr1ReportResponse getGstr1Report(Long businessId, String period);
    List<ReportDto.CustomerReportEntry> getCustomerWiseReport(Long businessId);
    List<ReportDto.ProductReportEntry> getProductWiseReport(Long businessId);
    List<ReportDto.OutstandingReportEntry> getOutstandingReport(Long businessId);
}

