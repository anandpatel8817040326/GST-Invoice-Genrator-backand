package com.gstinvoice.product.controller;

import com.gstinvoice.product.dto.ProductDto;
import com.gstinvoice.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Catalog", description = "Product CRUD, HSN/SAC, GST rates (0%, 5%, 12%, 18%, 28%) and pricing")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create product in catalog")
    public ResponseEntity<ProductDto.ApiResponse<ProductDto.ProductResponse>> createProduct(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @Valid @RequestBody ProductDto.ProductRequest request) {
        ProductDto.ProductResponse response = productService.createProduct(businessId, request);
        return new ResponseEntity<>(
                ProductDto.ApiResponse.<ProductDto.ProductResponse>builder()
                        .success(true)
                        .message("Product created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDto.ApiResponse<ProductDto.ProductResponse>> getProductById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false) Long businessId) {
        ProductDto.ProductResponse response = productService.getProductById(id, businessId);
        return ResponseEntity.ok(
                ProductDto.ApiResponse.<ProductDto.ProductResponse>builder()
                        .success(true)
                        .message("Product retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "List all products for business")
    public ResponseEntity<ProductDto.ApiResponse<List<ProductDto.ProductResponse>>> getProducts(
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @RequestParam(value = "search", required = false) String search) {
        List<ProductDto.ProductResponse> list = productService.getProductsByBusiness(businessId, search);
        return ResponseEntity.ok(
                ProductDto.ApiResponse.<List<ProductDto.ProductResponse>>builder()
                        .success(true)
                        .message("Products retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductDto.ApiResponse<ProductDto.ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId,
            @Valid @RequestBody ProductDto.ProductRequest request) {
        ProductDto.ProductResponse response = productService.updateProduct(id, businessId, request);
        return ResponseEntity.ok(
                ProductDto.ApiResponse.<ProductDto.ProductResponse>builder()
                        .success(true)
                        .message("Product updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ProductDto.ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "X-Business-Id", required = false, defaultValue = "1") Long businessId) {
        productService.deleteProduct(id, businessId);
        return ResponseEntity.ok(
                ProductDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Product deleted successfully")
                        .build()
        );
    }
}

