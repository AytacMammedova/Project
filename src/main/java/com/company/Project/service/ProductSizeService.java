package com.company.Project.service;

import com.company.Project.model.dto.ProductSizeDto;
import com.company.Project.model.entity.ProductSize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductSizeService {
    List<ProductSizeDto> getAvailableSizes(Integer productId);
    boolean isAvailable(Integer productId, String sizeName, Integer quantity);
    ProductSize findByProductAndSize(Integer productId, String sizeName);
}