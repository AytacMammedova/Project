package com.company.Project.repository;

import com.company.Project.model.entity.ProductBucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductBucketRepository extends JpaRepository<ProductBucket,Long> {
    Optional<ProductBucket> findByBucketIdAndProductId(Long bucketId, Integer productId);

}
