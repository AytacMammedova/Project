package com.company.Project.service.impl;

import com.company.Project.exceptions.ProductNotFoundException;
import com.company.Project.mapper.ProductSizeMapper;
import com.company.Project.model.dto.ProductSizeDto;
import com.company.Project.model.entity.ProductSize;
import com.company.Project.repository.ProductSizeRepository;
import com.company.Project.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductSizeMapper productSizeMapper;

    @Override
    public List<ProductSizeDto> getAvailableSizes(Long productId) {
        log.info("Getting available sizes for product: {}", productId);
        List<ProductSize> availableSizes = productSizeRepository.findAvailableStockByProductId(productId);
        return productSizeMapper.toProductSizeDtoList(availableSizes);
    }

    @Override
    public boolean isAvailable(Long productId, String sizeName, Integer quantity) {
        ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(productId, sizeName)
                .orElse(null);

        if (productSize == null || !productSize.getIsAvailable()) {
            return false;
        }

        return productSize.getStockQuantity() >= quantity;
    }

    @Override
    public ProductSize findByProductAndSize(Long productId, String sizeName) {
        return productSizeRepository.findByProductIdAndSizeName(productId, sizeName)
                .orElseThrow(() -> new ProductNotFoundException("Size " + sizeName + " not found for product " + productId));
    }
}