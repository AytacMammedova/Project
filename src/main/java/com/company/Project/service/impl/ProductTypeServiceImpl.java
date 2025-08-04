package com.company.Project.service.impl;

import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductType;
import com.company.Project.repository.ProductTypeRepository;
import com.company.Project.service.ProductService;
import com.company.Project.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {
    private final ProductTypeRepository productTypeRepository;

    @Override
    public List<ProductType> getProductTypeList() {
        log.info("List of Product Types");
        return productTypeRepository.findAll();
    }

    @Override
    public ProductType getById(Integer id) {
        return productTypeRepository.findById(id).orElseThrow(IllegalStateException::new);
    }

    @Override
    public ProductType add(ProductType productType) {
        return productTypeRepository.save(productType);
    }

    @Override
    public ProductType update(Integer id, ProductType productType) {
        ProductType productType1=productTypeRepository.findById(id).orElseThrow(IllegalStateException::new);
        productType1.setName(productType.getName());
        return productTypeRepository.save(productType1);
    }

    @Override
    public void delete(Integer id) {
        log.info("Product Type deleted");
        productTypeRepository.deleteById(id);

    }
}
