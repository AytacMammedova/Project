package com.company.Project.repository;

import com.company.Project.model.entity.ProductBucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductBucketRepository extends JpaRepository<ProductBucket,Long> {
    List<ProductBucket> findByBucketIdAndProductId(Long bucketId, Long productId);

    boolean existsByBucketIdAndProductId(Long bucketId, Long productId);

    @Query("SELECT MAX(pb.bucketSequence) FROM ProductBucket pb WHERE pb.bucket.id = :bucketId")
    Integer findMaxSequenceByBucketId(@Param("bucketId") Long bucketId);

    @Query("SELECT pb FROM ProductBucket pb WHERE pb.bucket.id = :bucketId ORDER BY pb.bucketSequence")
    List<ProductBucket> findByBucketIdOrderBySequence(@Param("bucketId") Long bucketId);

    @Modifying
    @Query("DELETE FROM ProductBucket pb WHERE pb.bucket.id = :bucketId")
    void deleteByBucketId(@Param("bucketId") Long bucketId);


    @Query("SELECT pb FROM ProductBucket pb WHERE pb.bucket.id = :bucketId AND pb.product.id = :productId AND pb.sizeName = :sizeName")
    Optional<ProductBucket> findByBucketIdAndProductIdAndSize(@Param("bucketId") Long bucketId, @Param("productId") Long productId, @Param("sizeName") String sizeName);
    

}
