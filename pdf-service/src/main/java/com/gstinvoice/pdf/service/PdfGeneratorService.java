package com.gstinvoice.pdf.service;

import com.gstinvoice.pdf.dto.PdfInvoicePayload;

public interface PdfGeneratorService {
    byte[] generateInvoicePdf(PdfInvoicePayload invoice);
    byte[] getInvoicePdfById(Long invoiceId);
}

