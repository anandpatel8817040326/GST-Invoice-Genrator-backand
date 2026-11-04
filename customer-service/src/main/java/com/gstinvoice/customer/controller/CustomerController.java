package com.gstinvoice.customer.controller;

import com.gstinvoice.customer.dto.CustomerDto;
import com.gstinvoice.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "Customer CRUD, GSTIN directory, Place of supply state code")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Create new customer")
    public ResponseEntity<CustomerDto.ApiResponse<CustomerDto.CustomerResponse>> createCustomer(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @Valid @RequestBody CustomerDto.CustomerRequest request) {
        CustomerDto.CustomerResponse response = customerService.createCustomer(businessId, request);
        return new ResponseEntity<>(
                CustomerDto.ApiResponse.<CustomerDto.CustomerResponse>builder()
                        .success(true)
                        .message("Customer created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerDto.ApiResponse<CustomerDto.CustomerResponse>> getCustomerById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId) {
        CustomerDto.CustomerResponse response = customerService.getCustomerById(id, businessId);
        return ResponseEntity.ok(
                CustomerDto.ApiResponse.<CustomerDto.CustomerResponse>builder()
                        .success(true)
                        .message("Customer retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "List all customers for business")
    public ResponseEntity<CustomerDto.ApiResponse<List<CustomerDto.CustomerResponse>>> getCustomers(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestParam(value = "search", required = false) String search) {
        List<CustomerDto.CustomerResponse> list = customerService.getCustomersByBusiness(businessId, search);
        return ResponseEntity.ok(
                CustomerDto.ApiResponse.<List<CustomerDto.CustomerResponse>>builder()
                        .success(true)
                        .message("Customers retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerDto.ApiResponse<CustomerDto.CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @Valid @RequestBody CustomerDto.CustomerRequest request) {
        CustomerDto.CustomerResponse response = customerService.updateCustomer(id, businessId, request);
        return ResponseEntity.ok(
                CustomerDto.ApiResponse.<CustomerDto.CustomerResponse>builder()
                        .success(true)
                        .message("Customer updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<CustomerDto.ApiResponse<Void>> deleteCustomer(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        customerService.deleteCustomer(id, businessId);
        return ResponseEntity.ok(
                CustomerDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Customer deleted successfully")
                        .build()
        );
    }
}

