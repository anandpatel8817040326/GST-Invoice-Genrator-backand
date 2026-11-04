package com.gstinvoice.invoice.service.impl;

import com.gstinvoice.invoice.client.BusinessClient;
import com.gstinvoice.invoice.client.CustomerClient;
import com.gstinvoice.invoice.client.GstClient;
import com.gstinvoice.invoice.client.ProductClient;
import com.gstinvoice.invoice.dto.InvoiceDto;
import com.gstinvoice.invoice.entity.*;
import com.gstinvoice.invoice.exception.BadRequestException;
import com.gstinvoice.invoice.exception.ResourceNotFoundException;
import com.gstinvoice.invoice.repository.InvoiceHistoryRepository;
import com.gstinvoice.invoice.repository.InvoiceRepository;
import com.gstinvoice.invoice.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceHistoryRepository historyRepository;
    private final BusinessClient businessClient;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final GstClient gstClient;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceHistoryRepository historyRepository,
                              BusinessClient businessClient,
                              CustomerClient customerClient,
                              ProductClient productClient,
                              GstClient gstClient) {
        this.invoiceRepository = invoiceRepository;
        this.historyRepository = historyRepository;
        this.businessClient = businessClient;
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.gstClient = gstClient;
    }

    @Override
    public InvoiceDto.InvoiceResponse createInvoice(Long businessId, InvoiceDto.CreateInvoiceRequest request, String userEmail) {
        // 1. Fetch Business snapshot
        BusinessClient.BusinessClientDto business = null;
        try {
            var bRes = businessClient.getBusinessById(businessId);
            if (bRes != null && bRes.getData() != null) {
                business = bRes.getData();
            }
        } catch (Exception ignored) {}

        String sellerName = (business != null) ? business.getBusinessName() : "GST Enterprise";
        String sellerGstin = (business != null) ? business.getGstin() : "27AAAAA0000A1Z5";
        String sellerAddress = (business != null) ? (business.getAddressLine1() + ", " + business.getCity()) : "Mumbai";
        String sellerState = (business != null) ? business.getState() : "Maharashtra";
        String sellerStateCode = (business != null && business.getStateCode() != null) ? business.getStateCode() : "27";
        String invoicePrefix = (business != null && business.getInvoicePrefix() != null) ? business.getInvoicePrefix() : "INV-";

        // 2. Fetch Customer snapshot
        CustomerClient.CustomerClientDto customer = null;
        try {
            var cRes = customerClient.getCustomerById(request.getCustomerId());
            if (cRes != null && cRes.getData() != null) {
                customer = cRes.getData();
            }
        } catch (Exception ignored) {}

        String buyerName = (customer != null) ? customer.getCustomerName() : "Valued Customer";
        String buyerGstin = (customer != null) ? customer.getGstin() : null;
        String buyerAddress = (customer != null) ? (customer.getAddressLine1() + ", " + customer.getCity()) : "Customer City";
        String buyerState = (customer != null) ? customer.getState() : "Maharashtra";
        String buyerStateCode = (customer != null && customer.getStateCode() != null) ? customer.getStateCode() : sellerStateCode;

        String placeOfSupply = (request.getPlaceOfSupply() != null && !request.getPlaceOfSupply().isEmpty())
                ? request.getPlaceOfSupply()
                : buyerState;

        // 3. Prepare Tax Calculation via GST Service
        List<GstClient.GstCalculationItem> calcItems = request.getItems().stream().map(item ->
                GstClient.GstCalculationItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .hsnSac(item.getHsnSac())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .gstRate(item.getGstRate())
                        .build()
        ).collect(Collectors.toList());

        GstClient.GstCalculationRequest calcRequest = GstClient.GstCalculationRequest.builder()
                .sellerStateCode(sellerStateCode)
                .buyerStateCode(buyerStateCode)
                .items(calcItems)
                .overallDiscount(request.getOverallDiscount())
                .build();

        GstClient.GstCalculationResponse calcResponse = null;
        try {
            var gRes = gstClient.calculate(calcRequest);
            if (gRes != null && gRes.getData() != null) {
                calcResponse = gRes.getData();
            }
        } catch (Exception ignored) {}

        // Fallback calculation in case GST service is starting up
        if (calcResponse == null) {
            calcResponse = fallbackCalculation(calcRequest);
        }

        // 4. Generate sequential invoice number if not provided
        String invoiceNumber = request.getCustomInvoiceNumber();
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            long count = invoiceRepository.countByBusinessId(businessId) + 1;
            invoiceNumber = String.format("%s%d-%04d", invoicePrefix, LocalDate.now().getYear(), count);
        }

        LocalDate invDate = request.getInvoiceDate() != null ? request.getInvoiceDate() : LocalDate.now();
        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : invDate.plusDays(30);

        Invoice invoice = Invoice.builder()
                .businessId(businessId)
                .customerId(request.getCustomerId())
                .invoiceNumber(invoiceNumber)
                .invoiceDate(invDate)
                .dueDate(dueDate)
                .sellerName(sellerName)
                .sellerGstin(sellerGstin)
                .sellerAddress(sellerAddress)
                .sellerState(sellerState)
                .sellerStateCode(sellerStateCode)
                .buyerName(buyerName)
                .buyerGstin(buyerGstin)
                .buyerAddress(buyerAddress)
                .buyerState(buyerState)
                .buyerStateCode(buyerStateCode)
                .placeOfSupply(placeOfSupply)
                .reverseCharge(request.getReverseCharge() != null ? request.getReverseCharge() : false)
                .taxableSubtotal(calcResponse.getTaxableSubtotal())
                .totalDiscount(calcResponse.getTotalDiscount())
                .totalCgst(calcResponse.getTotalCgst())
                .totalSgst(calcResponse.getTotalSgst())
                .totalIgst(calcResponse.getTotalIgst())
                .totalTax(calcResponse.getTotalTax())
                .roundOff(calcResponse.getRoundOff())
                .grandTotal(calcResponse.getGrandTotal())
                .paidAmount(BigDecimal.ZERO)
                .balanceDue(calcResponse.getGrandTotal())
                .amountInWords(calcResponse.getAmountInWords())
                .notes(request.getNotes())
                .termsAndConditions(request.getTermsAndConditions() != null ? request.getTermsAndConditions() : (business != null ? business.getTermsAndConditions() : "Standard Terms"))
                .status(InvoiceStatus.ISSUED)
                .paymentStatus(PaymentStatus.UNPAID)
                .items(new ArrayList<>())
                .build();

        // 5. Build and attach line items
        for (int i = 0; i < request.getItems().size(); i++) {
            var reqItem = request.getItems().get(i);
            var resItem = (calcResponse.getItems().size() > i) ? calcResponse.getItems().get(i) : null;

            InvoiceItem item = InvoiceItem.builder()
                    .productId(reqItem.getProductId())
                    .productName(reqItem.getProductName())
                    .description(reqItem.getDescription())
                    .hsnSac(reqItem.getHsnSac())
                    .quantity(reqItem.getQuantity())
                    .unit(reqItem.getUnit() != null ? reqItem.getUnit() : "NOS")
                    .unitPrice(reqItem.getUnitPrice())
                    .discount(reqItem.getDiscount() != null ? reqItem.getDiscount() : BigDecimal.ZERO)
                    .taxableAmount(resItem != null ? resItem.getTaxableAmount() : reqItem.getUnitPrice().multiply(reqItem.getQuantity()))
                    .gstRate(reqItem.getGstRate())
                    .cgstRate(resItem != null ? resItem.getCgstRate() : BigDecimal.ZERO)
                    .cgstAmount(resItem != null ? resItem.getCgstAmount() : BigDecimal.ZERO)
                    .sgstRate(resItem != null ? resItem.getSgstRate() : BigDecimal.ZERO)
                    .sgstAmount(resItem != null ? resItem.getSgstAmount() : BigDecimal.ZERO)
                    .igstRate(resItem != null ? resItem.getIgstRate() : BigDecimal.ZERO)
                    .igstAmount(resItem != null ? resItem.getIgstAmount() : BigDecimal.ZERO)
                    .totalTax(resItem != null ? resItem.getTotalTax() : BigDecimal.ZERO)
                    .totalAmount(resItem != null ? resItem.getTotalAmount() : reqItem.getUnitPrice().multiply(reqItem.getQuantity()))
                    .build();

            invoice.addItem(item);
        }

        Invoice saved = invoiceRepository.save(invoice);

        // Audit Trail
        historyRepository.save(InvoiceHistory.builder()
                .invoiceId(saved.getId())
                .action("CREATED")
                .remarks("Invoice generated with number " + saved.getInvoiceNumber() + ", Total: INR " + saved.getGrandTotal())
                .performedBy(userEmail != null ? userEmail : "SYSTEM")
                .build());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto.InvoiceResponse getInvoiceById(Long id, Long businessId) {
        Invoice invoice = (businessId != null
                ? invoiceRepository.findByIdAndBusinessId(id, businessId)
                : invoiceRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto.InvoiceResponse> getInvoicesByBusiness(Long businessId, String search) {
        List<Invoice> list;
        if (search != null && !search.trim().isEmpty()) {
            list = invoiceRepository.searchInvoices(businessId, search.trim());
        } else {
            list = invoiceRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InvoiceDto.InvoiceResponse updateDraftInvoice(Long id, Long businessId, InvoiceDto.CreateInvoiceRequest request, String userEmail) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT invoices can be edited. Current status: " + invoice.getStatus());
        }

        invoice.setNotes(request.getNotes());
        invoice.setTermsAndConditions(request.getTermsAndConditions());
        if (request.getDueDate() != null) {
            invoice.setDueDate(request.getDueDate());
        }

        Invoice updated = invoiceRepository.save(invoice);

        historyRepository.save(InvoiceHistory.builder()
                .invoiceId(updated.getId())
                .action("UPDATED")
                .remarks("Draft invoice modified")
                .performedBy(userEmail != null ? userEmail : "SYSTEM")
                .build());

        return mapToResponse(updated);
    }

    @Override
    public InvoiceDto.InvoiceResponse updateInvoiceStatus(Long id, Long businessId, InvoiceStatus status, String userEmail) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        invoice.setStatus(status);
        Invoice updated = invoiceRepository.save(invoice);

        historyRepository.save(InvoiceHistory.builder()
                .invoiceId(updated.getId())
                .action("STATUS_CHANGED")
                .remarks("Status updated to " + status.name())
                .performedBy(userEmail != null ? userEmail : "SYSTEM")
                .build());

        return mapToResponse(updated);
    }

    @Override
    public InvoiceDto.InvoiceResponse recordPayment(Long id, Long businessId, InvoiceDto.UpdatePaymentRequest request, String userEmail) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidTotal = currentPaid.add(request.getPaidAmount());

        if (newPaidTotal.compareTo(invoice.getGrandTotal()) > 0) {
            throw new BadRequestException("Paid amount cannot exceed total invoice balance due");
        }

        invoice.setPaidAmount(newPaidTotal);
        BigDecimal balance = invoice.getGrandTotal().subtract(newPaidTotal);
        invoice.setBalanceDue(balance);

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        Invoice updated = invoiceRepository.save(invoice);

        historyRepository.save(InvoiceHistory.builder()
                .invoiceId(updated.getId())
                .action("PAYMENT_RECORDED")
                .remarks("Payment recorded: INR " + request.getPaidAmount() + ". Remaining: INR " + balance + ". " + (request.getRemarks() != null ? request.getRemarks() : ""))
                .performedBy(userEmail != null ? userEmail : "SYSTEM")
                .build());

        return mapToResponse(updated);
    }

    @Override
    public void deleteDraftInvoice(Long id, Long businessId, String userEmail) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT invoices can be deleted");
        }

        invoiceRepository.delete(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceHistory> getInvoiceHistory(Long invoiceId) {
        return historyRepository.findByInvoiceIdOrderByCreatedAtDesc(invoiceId);
    }

    private GstClient.GstCalculationResponse fallbackCalculation(GstClient.GstCalculationRequest req) {
        boolean isIntra = req.getSellerStateCode() != null && req.getSellerStateCode().equalsIgnoreCase(req.getBuyerStateCode());
        BigDecimal taxable = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;
        List<GstClient.CalculatedItemResponse> items = new ArrayList<>();

        for (var item : req.getItems()) {
            BigDecimal lineTaxable = item.getUnitPrice().multiply(item.getQuantity()).subtract(item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);
            BigDecimal lineTax = lineTaxable.multiply(item.getGstRate()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal lineTotal = lineTaxable.add(lineTax);

            BigDecimal cRate = isIntra ? item.getGstRate().divide(BigDecimal.valueOf(2), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal cAmt = isIntra ? lineTax.divide(BigDecimal.valueOf(2), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal sRate = cRate;
            BigDecimal sAmt = isIntra ? lineTax.subtract(cAmt) : BigDecimal.ZERO;
            BigDecimal iRate = !isIntra ? item.getGstRate() : BigDecimal.ZERO;
            BigDecimal iAmt = !isIntra ? lineTax : BigDecimal.ZERO;

            taxable = taxable.add(lineTaxable);
            cgst = cgst.add(cAmt);
            sgst = sgst.add(sAmt);
            igst = igst.add(iAmt);

            var calcItem = new GstClient.CalculatedItemResponse();
            calcItem.setProductId(item.getProductId());
            calcItem.setProductName(item.getProductName());
            calcItem.setHsnSac(item.getHsnSac());
            calcItem.setQuantity(item.getQuantity());
            calcItem.setUnitPrice(item.getUnitPrice());
            calcItem.setDiscount(item.getDiscount());
            calcItem.setTaxableAmount(lineTaxable);
            calcItem.setGstRate(item.getGstRate());
            calcItem.setCgstRate(cRate);
            calcItem.setCgstAmount(cAmt);
            calcItem.setSgstRate(sRate);
            calcItem.setSgstAmount(sAmt);
            calcItem.setIgstRate(iRate);
            calcItem.setIgstAmount(iAmt);
            calcItem.setTotalTax(lineTax);
            calcItem.setTotalAmount(lineTotal);
            items.add(calcItem);
        }

        BigDecimal totalTax = cgst.add(sgst).add(igst);
        BigDecimal grand = taxable.add(totalTax);

        var resp = new GstClient.GstCalculationResponse();
        resp.setIntraState(isIntra);
        resp.setSellerStateCode(req.getSellerStateCode());
        resp.setBuyerStateCode(req.getBuyerStateCode());
        resp.setItems(items);
        resp.setTaxableSubtotal(taxable);
        resp.setTotalDiscount(BigDecimal.ZERO);
        resp.setTotalCgst(cgst);
        resp.setTotalSgst(sgst);
        resp.setTotalIgst(igst);
        resp.setTotalTax(totalTax);
        resp.setRoundOff(BigDecimal.ZERO);
        resp.setGrandTotal(grand);
        resp.setAmountInWords("Indian Rupees Only");
        return resp;
    }

    private InvoiceDto.InvoiceResponse mapToResponse(Invoice inv) {
        List<InvoiceDto.InvoiceItemResponse> itemDtos = inv.getItems().stream().map(item ->
                InvoiceDto.InvoiceItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .description(item.getDescription())
                        .hsnSac(item.getHsnSac())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .taxableAmount(item.getTaxableAmount())
                        .gstRate(item.getGstRate())
                        .cgstRate(item.getCgstRate())
                        .cgstAmount(item.getCgstAmount())
                        .sgstRate(item.getSgstRate())
                        .sgstAmount(item.getSgstAmount())
                        .igstRate(item.getIgstRate())
                        .igstAmount(item.getIgstAmount())
                        .totalTax(item.getTotalTax())
                        .totalAmount(item.getTotalAmount())
                        .build()
        ).collect(Collectors.toList());

        return InvoiceDto.InvoiceResponse.builder()
                .id(inv.getId())
                .businessId(inv.getBusinessId())
                .customerId(inv.getCustomerId())
                .invoiceNumber(inv.getInvoiceNumber())
                .invoiceDate(inv.getInvoiceDate())
                .dueDate(inv.getDueDate())
                .sellerName(inv.getSellerName())
                .sellerGstin(inv.getSellerGstin())
                .sellerAddress(inv.getSellerAddress())
                .sellerState(inv.getSellerState())
                .sellerStateCode(inv.getSellerStateCode())
                .buyerName(inv.getBuyerName())
                .buyerGstin(inv.getBuyerGstin())
                .buyerAddress(inv.getBuyerAddress())
                .buyerState(inv.getBuyerState())
                .buyerStateCode(inv.getBuyerStateCode())
                .placeOfSupply(inv.getPlaceOfSupply())
                .reverseCharge(inv.getReverseCharge())
                .taxableSubtotal(inv.getTaxableSubtotal())
                .totalDiscount(inv.getTotalDiscount())
                .totalCgst(inv.getTotalCgst())
                .totalSgst(inv.getTotalSgst())
                .totalIgst(inv.getTotalIgst())
                .totalTax(inv.getTotalTax())
                .roundOff(inv.getRoundOff())
                .grandTotal(inv.getGrandTotal())
                .paidAmount(inv.getPaidAmount())
                .balanceDue(inv.getBalanceDue())
                .amountInWords(inv.getAmountInWords())
                .notes(inv.getNotes())
                .termsAndConditions(inv.getTermsAndConditions())
                .status(inv.getStatus())
                .paymentStatus(inv.getPaymentStatus())
                .items(itemDtos)
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}

