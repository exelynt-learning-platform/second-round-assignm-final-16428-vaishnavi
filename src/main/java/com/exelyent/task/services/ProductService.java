package com.exelyent.task.services;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.ProductRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ApiResponse.ProductResponse createProduct(ProductRequest.Create request);

    ApiResponse.ProductResponse getProductById(Long id);

    ApiResponse.PageResponse<ApiResponse.ProductResponse> getAllProducts(Pageable pageable);

    ApiResponse.PageResponse<ApiResponse.ProductResponse> searchProducts(
            String category, BigDecimal minPrice, BigDecimal maxPrice, String keyword, Pageable pageable);

    ApiResponse.ProductResponse updateProduct(Long id, ProductRequest.Update request);

    void deleteProduct(Long id);

    ApiResponse.ProductResponse restoreProduct(Long id);
}