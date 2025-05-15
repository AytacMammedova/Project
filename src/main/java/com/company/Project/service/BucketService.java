package com.company.Project.service;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.request.ProductAddDto;
import org.springframework.stereotype.Service;

@Service
public interface BucketService {
    BucketDto getById(Long bucketId);
    BucketDto createBucket(Integer userId);
    void addProductToBucket(Long bucketId,Integer productId,Integer quantity);
    void deleteProductFromBucket(Long bucketId,Integer productId);
    void clearBucket(Long bucketId);

}
