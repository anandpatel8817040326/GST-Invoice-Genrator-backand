package com.gstinvoice.customer.repository;

import com.gstinvoice.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByBusinessId(Long businessId);
    Optional<Customer> findByIdAndBusinessId(Long id, Long businessId);
    List<Customer> findByBusinessIdAndCustomerNameContainingIgnoreCase(Long businessId, String customerName);
}

