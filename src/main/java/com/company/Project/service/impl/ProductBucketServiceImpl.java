package com.company.Project.service.impl;

import com.company.Project.mapper.ProductBucketMapper;
import com.company.Project.model.dto.ProductBucketDto;
import com.company.Project.repository.ProductBucketRepository;
import com.company.Project.service.ProductBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBucketServiceImpl implements ProductBucketService {
    private final ProductBucketMapper productBucketMapper;
    private final ProductBucketRepository productBucketRepository;
    @Override
    public List<ProductBucketDto> getListOfProductsInBucket() {
        return  productBucketMapper.toProductBucketDtoList(productBucketRepository.findAll());
    }
}
