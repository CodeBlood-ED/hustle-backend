package com.hustle.backend.modules.product.controller;

import com.hustle.backend.common.ApiResponse;
import com.hustle.backend.modules.product.dto.*;
import com.hustle.backend.modules.product.service.FileStorageService;
import com.hustle.backend.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Products", description = "Endpoints for managing and querying products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    // ==========================================
    // Legacy Admin Endpoints (hustle_admin compatible)
    // ==========================================

    @PostMapping(value = "/upload_product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a product with image (Hustle Admin endpoint)")
    public ResponseEntity<ProductLegacyResponse> uploadProduct(@ModelAttribute ProductLegacyRequest request) {
        ProductLegacyResponse response = productService.saveProductLegacy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get_products")
    @Operation(summary = "Retrieve all products (Hustle Admin endpoint)")
    public ResponseEntity<List<ProductLegacyResponse>> getProductsLegacy() {
        List<ProductLegacyResponse> products = productService.getAllProductsLegacy();
        return ResponseEntity.ok(products);
    }

    // ==========================================
    // Standard REST Endpoints (Hustle Web & API clients)
    // ==========================================

    @GetMapping("/products")
    @Operation(summary = "Get products with optional category, search keyword, or tag filter")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag) {
        List<ProductResponseDto> products = productService.getProducts(category, search, tag);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @PostMapping("/products")
    @Operation(summary = "Create product via JSON payload")
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponseDto product = productService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.ok("Product created successfully", product), HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update product details")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponseDto product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }

    @GetMapping("/products/categories")
    @Operation(summary = "Get catalog categories with item counts")
    public ResponseEntity<ApiResponse<List<CategorySummaryDto>>> getCategorySummaries() {
        List<CategorySummaryDto> categories = productService.getCategorySummaries();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @GetMapping("/products/images/{fileName:.+}")
    @Operation(summary = "Download or stream product image")
    public ResponseEntity<Resource> getProductImage(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ignored) {
        }

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
