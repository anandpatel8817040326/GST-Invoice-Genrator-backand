package com.gstinvoice.pdf.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfInvoicePayload {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String sellerName;
    private String sellerGstin;
    private String sellerPan;
    private String sellerAddress;
    private String sellerState;
    private String sellerStateCode;
    private String sellerPhone;
    private String sellerEmail;
    private String buyerName;
    private String buyerGstin;
    private String buyerAddress;
    private String buyerState;
    private String buyerStateCode;
    private String placeOfSupply;
    private Boolean reverseCharge;
    private BigDecimal taxableSubtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalCgst;
    private BigDecimal totalSgst;
    private BigDecimal totalIgst;
    private BigDecimal totalTax;
    private BigDecimal roundOff;
    private BigDecimal grandTotal;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private String amountInWords;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branchName;
    private String upiId;
    private String notes;
    private String termsAndConditions;
    private List<PdfInvoiceItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PdfInvoiceItem {
        private String productName;
        private String description;
        private String hsnSac;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal taxableAmount;
        private BigDecimal gstRate;
        private BigDecimal cgstRate;
        private BigDecimal cgstAmount;
        private BigDecimal sgstRate;
        private BigDecimal sgstAmount;
        private BigDecimal igstRate;
        private BigDecimal igstAmount;
        private BigDecimal totalTax;
        private BigDecimal totalAmount;
    }
}

