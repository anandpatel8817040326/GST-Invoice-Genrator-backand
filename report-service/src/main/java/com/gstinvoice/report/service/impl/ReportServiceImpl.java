package com.gstinvoice.report.service.impl;

import com.gstinvoice.report.client.InvoiceClient;
import com.gstinvoice.report.client.PaymentClient;
import com.gstinvoice.report.dto.ReportDto;
import com.gstinvoice.report.entity.ReportSnapshot;
import com.gstinvoice.report.repository.ReportSnapshotRepository;
import com.gstinvoice.report.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final InvoiceClient invoiceClient;
    private final PaymentClient paymentClient;
    private final ReportSnapshotRepository snapshotRepository;

    public ReportServiceImpl(InvoiceClient invoiceClient, PaymentClient paymentClient, ReportSnapshotRepository snapshotRepository) {
        this.invoiceClient = invoiceClient;
        this.paymentClient = paymentClient;
        this.snapshotRepository = snapshotRepository;
    }

    private List<InvoiceClient.InvoiceSummaryDto> fetchInvoices(Long businessId) {
        try {
            var res = invoiceClient.getInvoices(businessId);
            if (res != null && res.getData() != null) {
                return res.getData();
            }
        } catch (Exception ignored) {}
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto.DashboardMetricsResponse getDashboardMetrics(Long businessId) {
        List<InvoiceClient.InvoiceSummaryDto> invoices = fetchInvoices(businessId);

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        int paidCount = 0;
        int pendingCount = 0;

        Map<String, ReportDto.MonthlyTrend> trendMap = new LinkedHashMap<>();

        for (var inv : invoices) {
            BigDecimal gTotal = inv.getGrandTotal() != null ? inv.getGrandTotal() : BigDecimal.ZERO;
            BigDecimal tTax = inv.getTotalTax() != null ? inv.getTotalTax() : BigDecimal.ZERO;
            BigDecimal pAmt = inv.getPaidAmount() != null ? inv.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal bDue = inv.getBalanceDue() != null ? inv.getBalanceDue() : gTotal.subtract(pAmt);

            totalSales = totalSales.add(gTotal);
            totalTax = totalTax.add(tTax);
            totalPaid = totalPaid.add(pAmt);
            totalOutstanding = totalOutstanding.add(bDue);

            if ("PAID".equalsIgnoreCase(inv.getStatus()) || "PAID".equalsIgnoreCase(inv.getPaymentStatus())) {
                paidCount++;
            } else {
                pendingCount++;
            }

            // Monthly breakdown
            String mKey = (inv.getInvoiceDate() != null) ? inv.getInvoiceDate().format(DateTimeFormatter.ofPattern("MMM yyyy")) : "Current";
            trendMap.putIfAbsent(mKey, ReportDto.MonthlyTrend.builder()
                    .month(mKey)
                    .sales(BigDecimal.ZERO)
                    .tax(BigDecimal.ZERO)
                    .invoiceCount(0)
                    .build());

            var tr = trendMap.get(mKey);
            tr.setSales(tr.getSales().add(gTotal));
            tr.setTax(tr.getTax().add(tTax));
            tr.setInvoiceCount(tr.getInvoiceCount() + 1);
        }

        return ReportDto.DashboardMetricsResponse.builder()
                .totalSales(totalSales)
                .totalTaxCollected(totalTax)
                .totalPaidReceived(totalPaid)
                .totalOutstanding(totalOutstanding)
                .totalInvoices(invoices.size())
                .paidInvoicesCount(paidCount)
                .pendingInvoicesCount(pendingCount)
                .monthlyTrends(new ArrayList<>(trendMap.values()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto.Gstr1ReportResponse getGstr1Report(Long businessId, String period) {
        List<InvoiceClient.InvoiceSummaryDto> invoices = fetchInvoices(businessId);

        BigDecimal totalTaxable = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        List<ReportDto.Gstr1B2BEntry> b2bList = new ArrayList<>();
        Map<String, ReportDto.Gstr1B2CEntry> b2cMap = new HashMap<>();

        for (var inv : invoices) {
            BigDecimal taxable = inv.getTaxableSubtotal() != null ? inv.getTaxableSubtotal() : BigDecimal.ZERO;
            BigDecimal cgst = inv.getTotalCgst() != null ? inv.getTotalCgst() : BigDecimal.ZERO;
            BigDecimal sgst = inv.getTotalSgst() != null ? inv.getTotalSgst() : BigDecimal.ZERO;
            BigDecimal igst = inv.getTotalIgst() != null ? inv.getTotalIgst() : BigDecimal.ZERO;
            BigDecimal tax = inv.getTotalTax() != null ? inv.getTotalTax() : BigDecimal.ZERO;
            BigDecimal totalVal = inv.getGrandTotal() != null ? inv.getGrandTotal() : BigDecimal.ZERO;

            totalTaxable = totalTaxable.add(taxable);
            totalCgst = totalCgst.add(cgst);
            totalSgst = totalSgst.add(sgst);
            totalIgst = totalIgst.add(igst);
            totalTax = totalTax.add(tax);

            boolean isB2b = inv.getBuyerGstin() != null && !inv.getBuyerGstin().trim().isEmpty();
            if (isB2b) {
                b2bList.add(ReportDto.Gstr1B2BEntry.builder()
                        .customerName(inv.getBuyerName())
                        .customerGstin(inv.getBuyerGstin())
                        .invoiceNumber(inv.getInvoiceNumber())
                        .invoiceDate(inv.getInvoiceDate() != null ? inv.getInvoiceDate().toString() : "")
                        .placeOfSupply(inv.getPlaceOfSupply())
                        .taxableValue(taxable)
                        .totalTax(tax)
                        .invoiceValue(totalVal)
                        .build());
            } else {
                String pos = (inv.getPlaceOfSupply() != null) ? inv.getPlaceOfSupply() : "Other";
                b2cMap.putIfAbsent(pos, ReportDto.Gstr1B2CEntry.builder()
                        .placeOfSupply(pos)
                        .taxableValue(BigDecimal.ZERO)
                        .totalTax(BigDecimal.ZERO)
                        .totalValue(BigDecimal.ZERO)
                        .invoiceCount(0)
                        .build());

                var entry = b2cMap.get(pos);
                entry.setTaxableValue(entry.getTaxableValue().add(taxable));
                entry.setTotalTax(entry.getTotalTax().add(tax));
                entry.setTotalValue(entry.getTotalValue().add(totalVal));
                entry.setInvoiceCount(entry.getInvoiceCount() + 1);
            }
        }

        return ReportDto.Gstr1ReportResponse.builder()
                .businessId(businessId)
                .period(period != null ? period : "Current Fiscal Year")
                .totalTaxableValue(totalTaxable)
                .totalCgst(totalCgst)
                .totalSgst(totalSgst)
                .totalIgst(totalIgst)
                .totalTax(totalTax)
                .b2bInvoices(b2bList)
                .b2cInvoices(new ArrayList<>(b2cMap.values()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.CustomerReportEntry> getCustomerWiseReport(Long businessId) {
        List<InvoiceClient.InvoiceSummaryDto> invoices = fetchInvoices(businessId);
        Map<Long, ReportDto.CustomerReportEntry> map = new HashMap<>();

        for (var inv : invoices) {
            Long cId = inv.getCustomerId() != null ? inv.getCustomerId() : 0L;
            map.putIfAbsent(cId, ReportDto.CustomerReportEntry.builder()
                    .customerId(cId)
                    .customerName(inv.getBuyerName())
                    .gstin(inv.getBuyerGstin() != null ? inv.getBuyerGstin() : "Unregistered")
                    .invoiceCount(0)
                    .totalSales(BigDecimal.ZERO)
                    .totalPaid(BigDecimal.ZERO)
                    .outstandingBalance(BigDecimal.ZERO)
                    .build());

            var e = map.get(cId);
            e.setInvoiceCount(e.getInvoiceCount() + 1);
            e.setTotalSales(e.getTotalSales().add(inv.getGrandTotal() != null ? inv.getGrandTotal() : BigDecimal.ZERO));
            e.setTotalPaid(e.getTotalPaid().add(inv.getPaidAmount() != null ? inv.getPaidAmount() : BigDecimal.ZERO));
            e.setOutstandingBalance(e.getOutstandingBalance().add(inv.getBalanceDue() != null ? inv.getBalanceDue() : BigDecimal.ZERO));
        }

        return new ArrayList<>(map.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.ProductReportEntry> getProductWiseReport(Long businessId) {
        List<InvoiceClient.InvoiceSummaryDto> invoices = fetchInvoices(businessId);
        Map<String, ReportDto.ProductReportEntry> map = new HashMap<>();

        for (var inv : invoices) {
            if (inv.getItems() != null) {
                for (var item : inv.getItems()) {
                    String pName = (item.getProductName() != null) ? item.getProductName() : "Product";
                    map.putIfAbsent(pName, ReportDto.ProductReportEntry.builder()
                            .productName(pName)
                            .hsnSac(item.getHsnSac() != null ? item.getHsnSac() : "-")
                            .quantitySold(BigDecimal.ZERO)
                            .totalRevenue(BigDecimal.ZERO)
                            .totalTaxGenerated(BigDecimal.ZERO)
                            .build());

                    var pe = map.get(pName);
                    pe.setQuantitySold(pe.getQuantitySold().add(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO));
                    pe.setTotalRevenue(pe.getTotalRevenue().add(item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO));
                    pe.setTotalTaxGenerated(pe.getTotalTaxGenerated().add(item.getTotalTax() != null ? item.getTotalTax() : BigDecimal.ZERO));
                }
            }
        }

        return new ArrayList<>(map.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.OutstandingReportEntry> getOutstandingReport(Long businessId) {
        List<InvoiceClient.InvoiceSummaryDto> invoices = fetchInvoices(businessId);

        return invoices.stream()
                .filter(inv -> inv.getBalanceDue() != null && inv.getBalanceDue().compareTo(BigDecimal.ZERO) > 0)
                .map(inv -> ReportDto.OutstandingReportEntry.builder()
                        .invoiceId(inv.getId())
                        .invoiceNumber(inv.getInvoiceNumber())
                        .customerName(inv.getBuyerName())
                        .invoiceDate(inv.getInvoiceDate() != null ? inv.getInvoiceDate().toString() : "")
                        .dueDate(inv.getInvoiceDate() != null ? inv.getInvoiceDate().plusDays(30).toString() : "")
                        .grandTotal(inv.getGrandTotal())
                        .paidAmount(inv.getPaidAmount())
                        .balanceDue(inv.getBalanceDue())
                        .status(inv.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}

