package com.gstinvoice.product.repository;

import com.gstinvoice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusinessId(Long businessId);
    Optional<Product> findByIdAndBusinessId(Long id, Long businessId);
    List<Product> findByBusinessIdAndProductNameContainingIgnoreCase(Long businessId, String productName);
}

