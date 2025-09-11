package com.company.Project.repository;

import com.company.Project.model.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    List<ProductSize> findByProductIdAndIsAvailable(Long productId, Boolean isAvailable);

    Optional<ProductSize> findByProductIdAndSizeName(Long productId, String sizeName);

    @Query("SELECT SUM(ps.stockQuantity) FROM ProductSize ps WHERE ps.productId = :productId")
    Integer getTotalStockForProduct(@Param("productId") Integer productId);

    @Query("SELECT ps FROM ProductSize ps WHERE ps.productId = :productId AND ps.stockQuantity > 0")
    List<ProductSize> findAvailableStockByProductId(@Param("productId") Long productId);
}
