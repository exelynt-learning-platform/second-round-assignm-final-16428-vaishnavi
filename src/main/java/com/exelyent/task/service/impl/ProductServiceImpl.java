package com.exelyent.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.ProductRequest;
import com.exelyent.task.entity.Product;
import com.exelyent.task.exception.ResourceNotFoundException;
import com.exelyent.task.repository.ProductRepo;
import com.exelyent.task.services.ProductService;

import jakarta.validation.Valid;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepo productRepository;

    public ProductServiceImpl(ProductRepo productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ApiResponse.ProductResponse createProduct(ProductRequest.Create request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setActive(true);

        product = productRepository.save(product);
        log.info("Product created: id={}, name={}", product.getId(), product.getName());
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.PageResponse<ApiResponse.ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findByActiveTrue(pageable);
        return buildPage(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.PageResponse<ApiResponse.ProductResponse> searchProducts(
            String category, BigDecimal minPrice, BigDecimal maxPrice, String keyword, Pageable pageable) {
        Page<Product> page = productRepository.findByFilters(category, minPrice, maxPrice, keyword, pageable);
        return buildPage(page);
    }

    @Override
    @Transactional
    public ApiResponse.ProductResponse updateProduct( @PathVariable("id") Long id, @Valid @RequestBody ProductRequest.Update request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (request.getName() != null)          product.setName(request.getName());
        if (request.getDescription() != null)   product.setDescription(request.getDescription());
        if (request.getPrice() != null)         product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getImageUrl() != null)      product.setImageUrl(request.getImageUrl());
        if (request.getCategory() != null)      product.setCategory(request.getCategory());
        if (request.getBrand() != null)         product.setBrand(request.getBrand());
        if (request.getActive() != null)        product.setActive(request.getActive());

        product = productRepository.save(product);
        log.info("Product updated: id={}", product.getId());
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(@PathVariable("id") Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.softDeleteById(id);
        log.info("Product soft-deleted: id={}", id);
    }

    @Override
    @Transactional
    public ApiResponse.ProductResponse restoreProduct(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(true);
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    // ─── Mapper ─────────────────────────────────────────────────────────────

    private ApiResponse.ProductResponse mapToResponse(Product p) {
        ApiResponse.ProductResponse response = new ApiResponse.ProductResponse();
        response.setId(p.getId());
        response.setName(p.getName());
        response.setDescription(p.getDescription());
        response.setPrice(p.getPrice());
        response.setStockQuantity(p.getStockQuantity());
        response.setImageUrl(p.getImageUrl());
        response.setCategory(p.getCategory());
        response.setBrand(p.getBrand());
        response.setActive(p.isActive());
        response.setInStock(p.isInStock());
        response.setCreatedAt(p.getCreatedAt());
        response.setUpdatedAt(p.getUpdatedAt());
        return response;
    }

    private ApiResponse.PageResponse<ApiResponse.ProductResponse> buildPage(Page<Product> page) {
        ApiResponse.PageResponse<ApiResponse.ProductResponse> response = new ApiResponse.PageResponse<>();
        response.setContent(page.getContent().stream().map(this::mapToResponse).toList());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}