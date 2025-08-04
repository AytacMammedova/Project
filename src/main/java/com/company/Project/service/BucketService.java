package com.company.Project.service;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.ProductBucketId;
import org.springframework.stereotype.Service;

@Service
public interface BucketService {
    BucketDto getById(Long userId);
    BucketDto addProductToBucket(BucketAddDto bucketAddDto);
    void deleteProductFromBucket(Long bucketId, Long productBucketId);
    void clearBucket(Long bucketId);

}
