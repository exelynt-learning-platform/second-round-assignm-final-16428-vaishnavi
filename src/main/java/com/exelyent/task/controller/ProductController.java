package com.exelyent.task.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.ProductRequest;
import com.exelyent.task.services.ProductService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
    public ResponseEntity<ApiResponse.PageResponse<ApiResponse.ProductResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse.ProductResponse> getProductById(@PathVariable("id") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse.PageResponse<ApiResponse.ProductResponse>> searchProducts(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                productService.searchProducts(category, minPrice, maxPrice, keyword, pageable));
    }

    // ─── Admin ───────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse.ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse.ProductResponse> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest.Update request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse.ProductResponse> restoreProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.restoreProduct(id));
    }
}