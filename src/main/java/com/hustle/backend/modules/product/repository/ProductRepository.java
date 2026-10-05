package com.hustle.backend.modules.product.repository;

import com.hustle.backend.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByIdDesc();

    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);

    List<Product> findByTagInAndActiveTrue(Collection<String> tags);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("query") String query);

    long countByCategoryIgnoreCaseAndActiveTrue(String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true")
    List<String> findDistinctCategories();
}
