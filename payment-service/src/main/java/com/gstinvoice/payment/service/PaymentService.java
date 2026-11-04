package com.gstinvoice.payment.service;

import com.gstinvoice.payment.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto.PaymentResponse recordPayment(Long businessId, PaymentDto.RecordPaymentRequest request, String userEmail);
    List<PaymentDto.PaymentResponse> getPaymentsByBusiness(Long businessId);
    List<PaymentDto.PaymentResponse> getPaymentsByInvoice(Long invoiceId);
    PaymentDto.PaymentResponse getPaymentById(Long id);
}

