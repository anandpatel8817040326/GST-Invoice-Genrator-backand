package com.gstinvoice.customer.service;

import com.gstinvoice.customer.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    CustomerDto.CustomerResponse createCustomer(Long businessId, CustomerDto.CustomerRequest request);
    CustomerDto.CustomerResponse getCustomerById(Long id, Long businessId);
    List<CustomerDto.CustomerResponse> getCustomersByBusiness(Long businessId, String query);
    CustomerDto.CustomerResponse updateCustomer(Long id, Long businessId, CustomerDto.CustomerRequest request);
    void deleteCustomer(Long id, Long businessId);
}

