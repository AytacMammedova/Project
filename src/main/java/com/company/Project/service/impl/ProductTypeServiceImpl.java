package com.company.Project.service.impl;

import com.company.Project.exceptions.CategoryNotFoundException;
import com.company.Project.mapper.ProductTypeMapper;
import com.company.Project.model.dto.ProductTypeDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductType;
import com.company.Project.repository.ProductTypeRepository;
import com.company.Project.service.ProductService;
import com.company.Project.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;

    @Override
    public List<ProductTypeDto> getProductTypeList() {
        log.info("Getting all Product Types");
        return productTypeMapper.toProductTypeDtoList(productTypeRepository.findAll());
    }

    @Override
    public ProductTypeDto getById(Integer id) {
        log.info("Getting ProductType by id: {}", id);
        return productTypeMapper.toProductTypeDto(productTypeRepository.findById(id)
                .orElseThrow(()->new CategoryNotFoundException("No ProductType with id+ "+id)));
    }

    @Override
    public ProductTypeDto add(ProductType productType) {
        log.info("Adding new ProductType: {}", productType.getName());
        if (productType.getName() == null || productType.getName().trim().isEmpty()) {
            throw new CategoryNotFoundException("No ProductType with name+ "+productType.getName());
        }
        productType.setName(productType.getName().trim());
        ProductType savedProductType = productTypeRepository.save(productType);
        log.info("ProductType added successfully with id: {}", savedProductType.getId());
        return productTypeMapper.toProductTypeDto(savedProductType);
    }

    @Override
    public ProductTypeDto update(Integer id, ProductType productType) {
        log.info("Updating ProductType with id: {}", id);
        ProductType existingProductType = productTypeRepository.findById(id)
                .orElseThrow(()->new CategoryNotFoundException("No ProductType with id+ "+id));

        if(Objects.nonNull(productType.getName()) && !productType.getName().trim().isEmpty()){
            existingProductType.setName(productType.getName().trim());
        }

        ProductType savedProductType = productTypeRepository.save(existingProductType);
        log.info("ProductType updated successfully");
        return productTypeMapper.toProductTypeDto(savedProductType);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting ProductType with id: {}", id);
        if (!productTypeRepository.existsById(id)) {
            throw new CategoryNotFoundException("No ProductType with id+ "+id) ;
        }
        productTypeRepository.deleteById(id);
        log.info("ProductType deleted successfully");

    }
}
