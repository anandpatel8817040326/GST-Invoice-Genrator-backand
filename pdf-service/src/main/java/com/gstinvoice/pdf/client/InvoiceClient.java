package com.gstinvoice.pdf.client;

import com.gstinvoice.pdf.dto.PdfInvoicePayload;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "invoice-service")
public interface InvoiceClient {

    @GetMapping("/api/invoices/{id}")
    ApiResponse<PdfInvoicePayload> getInvoiceById(@PathVariable("id") Long id);

    @Data
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
}

