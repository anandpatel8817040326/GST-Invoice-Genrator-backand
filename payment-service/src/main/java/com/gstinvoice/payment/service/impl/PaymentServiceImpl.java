package com.gstinvoice.payment.service.impl;

import com.gstinvoice.payment.client.InvoiceClient;
import com.gstinvoice.payment.dto.PaymentDto;
import com.gstinvoice.payment.entity.Payment;
import com.gstinvoice.payment.exception.ResourceNotFoundException;
import com.gstinvoice.payment.repository.PaymentRepository;
import com.gstinvoice.payment.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceClient invoiceClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository, InvoiceClient invoiceClient) {
        this.paymentRepository = paymentRepository;
        this.invoiceClient = invoiceClient;
    }

    @Override
    public PaymentDto.PaymentResponse recordPayment(Long businessId, PaymentDto.RecordPaymentRequest request, String userEmail) {
        String invoiceNumber = request.getInvoiceNumber();

        // 1. Fetch invoice info from invoice-service
        try {
            var invRes = invoiceClient.getInvoiceById(request.getInvoiceId());
            if (invRes != null && invRes.getData() != null) {
                invoiceNumber = invRes.getData().getInvoiceNumber();
            }
        } catch (Exception ignored) {}

        if (invoiceNumber == null) {
            invoiceNumber = "INV-" + request.getInvoiceId();
        }

        // 2. Generate receipt number
        long count = paymentRepository.countByBusinessId(businessId) + 1;
        String receiptNumber = String.format("RCP-%d-%04d", LocalDate.now().getYear(), count);

        LocalDate payDate = request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now();

        Payment payment = Payment.builder()
                .businessId(businessId)
                .invoiceId(request.getInvoiceId())
                .invoiceNumber(invoiceNumber)
                .receiptNumber(receiptNumber)
                .amount(request.getAmount())
                .paymentDate(payDate)
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .recordedBy(userEmail != null ? userEmail : "SYSTEM")
                .build();

        Payment saved = paymentRepository.save(payment);

        // 3. Notify invoice-service to deduct balance and update invoice status
        try {
            var updateDto = new InvoiceClient.PaymentUpdateDto();
            updateDto.setPaidAmount(request.getAmount());
            updateDto.setRemarks("Paid via " + request.getPaymentMethod() + " [Receipt: " + receiptNumber + "]");
            invoiceClient.recordPayment(request.getInvoiceId(), businessId, userEmail, updateDto);
        } catch (Exception ignored) {}

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByBusiness(Long businessId) {
        return paymentRepository.findByBusinessIdOrderByPaymentDateDesc(businessId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    private PaymentDto.PaymentResponse mapToResponse(Payment p) {
        return PaymentDto.PaymentResponse.builder()
                .id(p.getId())
                .businessId(p.getBusinessId())
                .invoiceId(p.getInvoiceId())
                .invoiceNumber(p.getInvoiceNumber())
                .receiptNumber(p.getReceiptNumber())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getPaymentMethod())
                .referenceNumber(p.getReferenceNumber())
                .notes(p.getNotes())
                .recordedBy(p.getRecordedBy())
                .createdAt(p.getCreatedAt())
                .build();
    }
}

