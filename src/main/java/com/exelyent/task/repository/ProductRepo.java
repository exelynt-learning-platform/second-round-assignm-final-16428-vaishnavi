package com.exelyent.task.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exelyent.task.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND (:category IS NULL OR p.category = :category)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> findByFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.category IS NOT NULL")
    List<String> findAllActiveCategories();

    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
}