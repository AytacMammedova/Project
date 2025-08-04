package com.company.Project.repository;

import com.company.Project.model.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BucketRepository extends JpaRepository<Bucket,Long> {
    Optional<Bucket> findBucketByUserId(Long userId);
    boolean existsByUserId(Long userId);


}
