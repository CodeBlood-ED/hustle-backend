package com.hustle.backend.modules.product.service;

import com.hustle.backend.common.exception.ResourceNotFoundException;
import com.hustle.backend.modules.product.dto.*;
import com.hustle.backend.modules.product.entity.Product;
import com.hustle.backend.modules.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ProductLegacyResponse saveProductLegacy(ProductLegacyRequest request) {
        String imageUrl = null;
        if (request.getProduct_image() != null && !request.getProduct_image().isEmpty()) {
            imageUrl = fileStorageService.storeFile(request.getProduct_image());
        }

        Product product = new Product();
        product.setTitle(request.getProduct_title());
        product.setCategory(request.getProduct_category());
        product.setMrp(request.getProduct_mrp());
        product.setNetPrice(request.getProduct_netPrice());
        product.setImageUrl(imageUrl);
        product.setActive(true);

        Product saved = productRepository.save(product);
        return mapToLegacyResponse(saved);
    }

    public List<ProductLegacyResponse> getAllProductsLegacy() {
        return productRepository.findByActiveTrueOrderByIdDesc().stream()
                .map(this::mapToLegacyResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setCategory(request.getCategory());
        product.setMrp(request.getMrp());
        product.setNetPrice(request.getNetPrice());
        product.setImageUrl(request.getImageUrl());
        product.setDescription(request.getDescription());
        product.setTag(request.getTag());
        product.setAccent(request.getAccent());
        if (request.getColors() != null) {
            product.setColors(String.join(",", request.getColors()));
        }
        if (request.getMaterials() != null) {
            product.setMaterials(String.join(",", request.getMaterials()));
        }
        if (request.getFeatures() != null) {
            product.setFeatures(String.join(",", request.getFeatures()));
        }
        product.setActive(true);

        Product saved = productRepository.save(product);
        return mapToDto(saved);
    }

    public List<ProductResponseDto> getProducts(String category, String search, String tag) {
        List<Product> products;

        if (search != null && !search.isBlank()) {
            products = productRepository.searchProducts(search.trim());
        } else if (category != null && !category.isBlank()) {
            products = productRepository.findByCategoryIgnoreCaseAndActiveTrue(category.trim());
        } else if (tag != null && !tag.isBlank()) {
            products = productRepository.findByTagInAndActiveTrue(Collections.singletonList(tag.trim()));
        } else {
            products = productRepository.findByActiveTrueOrderByIdDesc();
        }

        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setTitle(request.getTitle());
        product.setCategory(request.getCategory());
        product.setMrp(request.getMrp());
        product.setNetPrice(request.getNetPrice());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        product.setDescription(request.getDescription());
        product.setTag(request.getTag());
        product.setAccent(request.getAccent());
        if (request.getColors() != null) {
            product.setColors(String.join(",", request.getColors()));
        }
        if (request.getMaterials() != null) {
            product.setMaterials(String.join(",", request.getMaterials()));
        }
        if (request.getFeatures() != null) {
            product.setFeatures(String.join(",", request.getFeatures()));
        }

        Product updated = productRepository.save(product);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public List<CategorySummaryDto> getCategorySummaries() {
        Map<String, String[]> modelMetadata = new LinkedHashMap<>();
        modelMetadata.put("iphone-13", new String[]{"iPhone 13", "Classic everyday protection", "#0d917e"});
        modelMetadata.put("iphone-14", new String[]{"iPhone 14", "Clean design with everyday grip", "#fac5d2"});
        modelMetadata.put("iphone-15", new String[]{"iPhone 15", "Slim, premium, and modern", "#7c3aed"});
        modelMetadata.put("iphone-16", new String[]{"iPhone 16", "Fresh color stories and durable builds", "#60a5fa"});
        modelMetadata.put("iphone-16-pro", new String[]{"iPhone 16 Pro", "Advanced finish for pro users", "#1f2937"});
        modelMetadata.put("iphone-16-pro-max", new String[]{"iPhone 16 Pro Max", "Max protection and bigger screen styling", "#f59e0b"});
        modelMetadata.put("iphone-17", new String[]{"iPhone 17", "Next-gen style for your daily carry", "#f43f5e"});
        modelMetadata.put("iphone-17-pro", new String[]{"iPhone 17 Pro", "Performance-first premium cases", "#a78bfa"});
        modelMetadata.put("iphone-18", new String[]{"iPhone 18", "New-year essentials and sleek finishes", "#22d3ee"});
        modelMetadata.put("iphone-18-pro", new String[]{"iPhone 18 Pro", "Luxury protection for top-tier devices", "#0d917e"});

        List<CategorySummaryDto> result = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : modelMetadata.entrySet()) {
            String model = entry.getKey();
            String[] meta = entry.getValue();
            long count = productRepository.countByCategoryIgnoreCaseAndActiveTrue(model);
            result.add(new CategorySummaryDto(model, meta[0], meta[1], meta[2], count));
        }
        return result;
    }

    public ProductLegacyResponse mapToLegacyResponse(Product product) {
        return new ProductLegacyResponse(
                product.getId(),
                product.getTitle(),
                product.getCategory(),
                product.getMrp(),
                product.getNetPrice(),
                product.getImageUrl()
        );
    }

    public ProductResponseDto mapToDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setCategory(product.getCategory());
        dto.setMrp(product.getMrp());
        dto.setNetPrice(product.getNetPrice());
        dto.setNumericPrice(product.getNumericPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());
        dto.setTag(product.getTag());
        dto.setAccent(product.getAccent());
        if (product.getColors() != null && !product.getColors().isBlank()) {
            dto.setColors(product.getColors().split(","));
        } else {
            dto.setColors(new String[0]);
        }
        if (product.getMaterials() != null && !product.getMaterials().isBlank()) {
            dto.setMaterials(product.getMaterials().split(","));
        } else {
            dto.setMaterials(new String[0]);
        }
        if (product.getFeatures() != null && !product.getFeatures().isBlank()) {
            dto.setFeatures(product.getFeatures().split(","));
        } else {
            dto.setFeatures(new String[0]);
        }
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}
