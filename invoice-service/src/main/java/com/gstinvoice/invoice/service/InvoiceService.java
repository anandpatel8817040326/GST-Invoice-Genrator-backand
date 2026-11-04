package com.gstinvoice.invoice.service;

import com.gstinvoice.invoice.dto.InvoiceDto;
import com.gstinvoice.invoice.entity.InvoiceHistory;
import com.gstinvoice.invoice.entity.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceDto.InvoiceResponse createInvoice(Long businessId, InvoiceDto.CreateInvoiceRequest request, String userEmail);
    InvoiceDto.InvoiceResponse getInvoiceById(Long id, Long businessId);
    List<InvoiceDto.InvoiceResponse> getInvoicesByBusiness(Long businessId, String search);
    InvoiceDto.InvoiceResponse updateDraftInvoice(Long id, Long businessId, InvoiceDto.CreateInvoiceRequest request, String userEmail);
    InvoiceDto.InvoiceResponse updateInvoiceStatus(Long id, Long businessId, InvoiceStatus status, String userEmail);
    InvoiceDto.InvoiceResponse recordPayment(Long id, Long businessId, InvoiceDto.UpdatePaymentRequest request, String userEmail);
    void deleteDraftInvoice(Long id, Long businessId, String userEmail);
    List<InvoiceHistory> getInvoiceHistory(Long invoiceId);
}

