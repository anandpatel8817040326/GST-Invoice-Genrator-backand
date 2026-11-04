package com.gstinvoice.product.service.impl;

import com.gstinvoice.product.dto.ProductDto;
import com.gstinvoice.product.entity.Product;
import com.gstinvoice.product.exception.ResourceNotFoundException;
import com.gstinvoice.product.repository.ProductRepository;
import com.gstinvoice.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto.ProductResponse createProduct(Long businessId, ProductDto.ProductRequest request) {
        Product product = Product.builder()
                .businessId(businessId)
                .productName(request.getProductName().trim())
                .description(request.getDescription())
                .hsnSac(request.getHsnSac() != null ? request.getHsnSac().trim() : null)
                .unit(request.getUnit() != null ? request.getUnit().trim().toUpperCase() : "NOS")
                .unitPrice(request.getUnitPrice())
                .gstRate(request.getGstRate())
                .category(request.getCategory() != null ? request.getCategory().trim() : "Goods")
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto.ProductResponse getProductById(Long id, Long businessId) {
        Product product = (businessId != null
                ? productRepository.findByIdAndBusinessId(id, businessId)
                : productRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto.ProductResponse> getProductsByBusiness(Long businessId, String query) {
        List<Product> list;
        if (query != null && !query.trim().isEmpty()) {
            list = productRepository.findByBusinessIdAndProductNameContainingIgnoreCase(businessId, query.trim());
        } else {
            list = productRepository.findByBusinessId(businessId);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ProductDto.ProductResponse updateProduct(Long id, Long businessId, ProductDto.ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setProductName(request.getProductName().trim());
        product.setDescription(request.getDescription());
        product.setHsnSac(request.getHsnSac() != null ? request.getHsnSac().trim() : null);
        product.setUnit(request.getUnit() != null ? request.getUnit().trim().toUpperCase() : "NOS");
        product.setUnitPrice(request.getUnitPrice());
        product.setGstRate(request.getGstRate());
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory().trim());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id, Long businessId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    private ProductDto.ProductResponse mapToResponse(Product p) {
        return ProductDto.ProductResponse.builder()
                .id(p.getId())
                .businessId(p.getBusinessId())
                .productName(p.getProductName())
                .description(p.getDescription())
                .hsnSac(p.getHsnSac())
                .unit(p.getUnit())
                .unitPrice(p.getUnitPrice())
                .gstRate(p.getGstRate())
                .category(p.getCategory())
                .stockQuantity(p.getStockQuantity())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

