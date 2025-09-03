package com.company.Project.repository;

import com.company.Project.model.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BucketRepository extends JpaRepository<Bucket,Long> {
    Optional<Bucket> findBucketByUserId(Long userId);
    boolean existsByUserId(Long userId);
    @Query("SELECT b FROM Bucket b LEFT JOIN FETCH b.productBucketList WHERE b.id = :bucketId")
    Optional<Bucket> findByIdWithProducts(@Param("bucketId") Long bucketId);


}
