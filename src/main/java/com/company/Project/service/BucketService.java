package com.company.Project.service;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.OrderHistoryDto;
import com.company.Project.model.dto.request.BucketAddDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BucketService {
    BucketDto getById(Long userId);
    BucketDto addProductToBucket(BucketAddDto bucketAddDto);
    BucketDto updateProductQuantity(Long bucketId, Long productId, Integer newQuantity,String sizeName);
    void removeProductFromBucket(Long bucketId, Long productBucketId);
    void clearBucket(Long bucketId);
    List<OrderHistoryDto> getOrderHistory(Long userId);

}
