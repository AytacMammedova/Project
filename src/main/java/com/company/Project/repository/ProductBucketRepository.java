package com.company.Project.repository;

import com.company.Project.model.entity.ProductBucket;
import com.company.Project.model.entity.ProductBucketId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductBucketRepository extends JpaRepository<ProductBucket,Long> {
    Optional<ProductBucket> findByBucketIdAndProductId(Long bucketId, Integer productId);
    @Query("SELECT MAX(pb.id.bucketProductId) FROM ProductBucket pb WHERE pb.bucket.id = :bucketId")
    Long findMaxBucketProductIdByBucketId(@Param("bucketId") Long bucketId);
    boolean existsById(ProductBucketId productBucketId);
    ProductBucket deleteByBucketId(Long bucketId);


}
