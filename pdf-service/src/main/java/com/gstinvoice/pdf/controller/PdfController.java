package com.gstinvoice.pdf.controller;

import com.gstinvoice.pdf.dto.PdfInvoicePayload;
import com.gstinvoice.pdf.service.PdfGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF Invoice Generation", description = "High quality vector PDF invoice generation and binary streaming")
public class PdfController {

    private final PdfGeneratorService pdfGeneratorService;

    public PdfController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Download or stream PDF invoice by invoice ID")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long invoiceId) {
        byte[] pdfBytes = pdfGeneratorService.getInvoicePdfById(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "Invoice-" + invoiceId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate and stream PDF from custom invoice JSON payload directly")
    public ResponseEntity<byte[]> generateDirectPdf(@RequestBody PdfInvoicePayload invoice) {
        byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "Invoice") + ".pdf";
        headers.setContentDispositionFormData("inline", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}

