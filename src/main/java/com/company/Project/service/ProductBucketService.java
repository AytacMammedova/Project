package com.company.Project.service;

import com.company.Project.model.dto.ProductBucketDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductBucketService {
    List<ProductBucketDto>getListOfProductsInBucket();
}
