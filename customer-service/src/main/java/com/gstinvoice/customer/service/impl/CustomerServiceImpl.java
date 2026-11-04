package com.gstinvoice.customer.service.impl;

import com.gstinvoice.customer.dto.CustomerDto;
import com.gstinvoice.customer.entity.Customer;
import com.gstinvoice.customer.exception.ResourceNotFoundException;
import com.gstinvoice.customer.repository.CustomerRepository;
import com.gstinvoice.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerDto.CustomerResponse createCustomer(Long businessId, CustomerDto.CustomerRequest request) {
        Customer customer = Customer.builder()
                .businessId(businessId)
                .customerName(request.getCustomerName().trim())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .gstin(request.getGstin() != null ? request.getGstin().trim().toUpperCase() : null)
                .pan(request.getPan() != null ? request.getPan().trim().toUpperCase() : null)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState().trim())
                .stateCode(request.getStateCode().trim())
                .pinCode(request.getPinCode())
                .customerType(request.getCustomerType() != null ? request.getCustomerType() : "B2B")
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto.CustomerResponse getCustomerById(Long id, Long businessId) {
        Customer customer = (businessId != null
                ? customerRepository.findByIdAndBusinessId(id, businessId)
                : customerRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto.CustomerResponse> getCustomersByBusiness(Long businessId, String query) {
        List<Customer> list;
        if (query != null && !query.trim().isEmpty()) {
            list = customerRepository.findByBusinessIdAndCustomerNameContainingIgnoreCase(businessId, query.trim());
        } else {
            list = customerRepository.findByBusinessId(businessId);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CustomerDto.CustomerResponse updateCustomer(Long id, Long businessId, CustomerDto.CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setCustomerName(request.getCustomerName().trim());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setGstin(request.getGstin() != null ? request.getGstin().trim().toUpperCase() : null);
        customer.setPan(request.getPan() != null ? request.getPan().trim().toUpperCase() : null);
        customer.setAddressLine1(request.getAddressLine1());
        customer.setAddressLine2(request.getAddressLine2());
        customer.setCity(request.getCity());
        customer.setState(request.getState().trim());
        customer.setStateCode(request.getStateCode().trim());
        customer.setPinCode(request.getPinCode());
        if (request.getCustomerType() != null) {
            customer.setCustomerType(request.getCustomerType());
        }

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id, Long businessId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    private CustomerDto.CustomerResponse mapToResponse(Customer c) {
        return CustomerDto.CustomerResponse.builder()
                .id(c.getId())
                .businessId(c.getBusinessId())
                .customerName(c.getCustomerName())
                .email(c.getEmail())
                .mobile(c.getMobile())
                .gstin(c.getGstin())
                .pan(c.getPan())
                .addressLine1(c.getAddressLine1())
                .addressLine2(c.getAddressLine2())
                .city(c.getCity())
                .state(c.getState())
                .stateCode(c.getStateCode())
                .pinCode(c.getPinCode())
                .customerType(c.getCustomerType())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

