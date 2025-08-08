package com.company.Project.service;

import com.company.Project.model.dto.ProductTypeDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductTypeService {
    List<ProductTypeDto> getProductTypeList();
    ProductTypeDto getById(Integer id);
    ProductTypeDto add(ProductType productType);
    ProductTypeDto update(Integer id,ProductType productType);
    void delete(Integer id);
}
