package com.company.Project.service;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.BucketAddDto;
import org.springframework.stereotype.Service;

@Service
public interface BucketService {
    BucketDto getById(Long userId);
    BucketDto addProductToBucket(BucketAddDto bucketAddDto);
    BucketDto updateProductQuantity(Long bucketId, Long productId, Integer newQuantity,String sizeName);
    void removeProductFromBucket(Long bucketId, Long productBucketId);
    void clearBucket(Long bucketId);

}
