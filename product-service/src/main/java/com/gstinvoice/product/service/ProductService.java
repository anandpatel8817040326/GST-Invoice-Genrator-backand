package com.gstinvoice.product.service;

import com.gstinvoice.product.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto.ProductResponse createProduct(Long businessId, ProductDto.ProductRequest request);
    ProductDto.ProductResponse getProductById(Long id, Long businessId);
    List<ProductDto.ProductResponse> getProductsByBusiness(Long businessId, String query);
    ProductDto.ProductResponse updateProduct(Long id, Long businessId, ProductDto.ProductRequest request);
    void deleteProduct(Long id, Long businessId);
}

