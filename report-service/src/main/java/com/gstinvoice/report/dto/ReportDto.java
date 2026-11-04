package com.gstinvoice.report.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class ReportDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardMetricsResponse {
        private BigDecimal totalSales;
        private BigDecimal totalTaxCollected;
        private BigDecimal totalPaidReceived;
        private BigDecimal totalOutstanding;
        private int totalInvoices;
        private int paidInvoicesCount;
        private int pendingInvoicesCount;
        private List<MonthlyTrend> monthlyTrends;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private String month;
        private BigDecimal sales;
        private BigDecimal tax;
        private int invoiceCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Gstr1ReportResponse {
        private Long businessId;
        private String period;
        private BigDecimal totalTaxableValue;
        private BigDecimal totalCgst;
        private BigDecimal totalSgst;
        private BigDecimal totalIgst;
        private BigDecimal totalTax;
        private List<Gstr1B2BEntry> b2bInvoices;
        private List<Gstr1B2CEntry> b2cInvoices;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Gstr1B2BEntry {
        private String customerName;
        private String customerGstin;
        private String invoiceNumber;
        private String invoiceDate;
        private String placeOfSupply;
        private BigDecimal taxableValue;
        private BigDecimal totalTax;
        private BigDecimal invoiceValue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Gstr1B2CEntry {
        private String placeOfSupply;
        private BigDecimal taxableValue;
        private BigDecimal totalTax;
        private BigDecimal totalValue;
        private int invoiceCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerReportEntry {
        private Long customerId;
        private String customerName;
        private String gstin;
        private int invoiceCount;
        private BigDecimal totalSales;
        private BigDecimal totalPaid;
        private BigDecimal outstandingBalance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductReportEntry {
        private String productName;
        private String hsnSac;
        private BigDecimal quantitySold;
        private BigDecimal totalRevenue;
        private BigDecimal totalTaxGenerated;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OutstandingReportEntry {
        private Long invoiceId;
        private String invoiceNumber;
        private String customerName;
        private String invoiceDate;
        private String dueDate;
        private BigDecimal grandTotal;
        private BigDecimal paidAmount;
        private BigDecimal balanceDue;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
}

