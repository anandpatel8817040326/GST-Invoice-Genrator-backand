package com.gstinvoice.report.controller;

import com.gstinvoice.report.dto.ReportDto;
import com.gstinvoice.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Analytics & Reports", description = "Dashboard metrics, GSTR-1, customer-wise and product-wise revenue analytics")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high level dashboard KPIs (total sales, tax, collected, outstanding)")
    public ResponseEntity<ReportDto.ApiResponse<ReportDto.DashboardMetricsResponse>> getDashboard(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        ReportDto.DashboardMetricsResponse metrics = reportService.getDashboardMetrics(businessId);
        return ResponseEntity.ok(
                ReportDto.ApiResponse.<ReportDto.DashboardMetricsResponse>builder()
                        .success(true)
                        .message("Dashboard KPIs retrieved")
                        .data(metrics)
                        .build()
        );
    }

    @GetMapping("/gstr1")
    @Operation(summary = "Generate GSTR-1 statutory summary (B2B and B2C splits)")
    public ResponseEntity<ReportDto.ApiResponse<ReportDto.Gstr1ReportResponse>> getGstr1(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestParam(value = "period", required = false) String period) {
        ReportDto.Gstr1ReportResponse report = reportService.getGstr1Report(businessId, period);
        return ResponseEntity.ok(
                ReportDto.ApiResponse.<ReportDto.Gstr1ReportResponse>builder()
                        .success(true)
                        .message("GSTR-1 report generated")
                        .data(report)
                        .build()
        );
    }

    @GetMapping("/customers")
    @Operation(summary = "Get customer-wise revenue and outstanding receivables")
    public ResponseEntity<ReportDto.ApiResponse<List<ReportDto.CustomerReportEntry>>> getCustomerReport(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        List<ReportDto.CustomerReportEntry> list = reportService.getCustomerWiseReport(businessId);
        return ResponseEntity.ok(
                ReportDto.ApiResponse.<List<ReportDto.CustomerReportEntry>>builder()
                        .success(true)
                        .message("Customer-wise report generated")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/products")
    @Operation(summary = "Get product-wise sales performance")
    public ResponseEntity<ReportDto.ApiResponse<List<ReportDto.ProductReportEntry>>> getProductReport(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        List<ReportDto.ProductReportEntry> list = reportService.getProductWiseReport(businessId);
        return ResponseEntity.ok(
                ReportDto.ApiResponse.<List<ReportDto.ProductReportEntry>>builder()
                        .success(true)
                        .message("Product-wise report generated")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/outstanding")
    @Operation(summary = "Get list of overdue and outstanding invoices")
    public ResponseEntity<ReportDto.ApiResponse<List<ReportDto.OutstandingReportEntry>>> getOutstandingReport(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        List<ReportDto.OutstandingReportEntry> list = reportService.getOutstandingReport(businessId);
        return ResponseEntity.ok(
                ReportDto.ApiResponse.<List<ReportDto.OutstandingReportEntry>>builder()
                        .success(true)
                        .message("Outstanding balance report generated")
                        .data(list)
                        .build()
        );
    }
}

