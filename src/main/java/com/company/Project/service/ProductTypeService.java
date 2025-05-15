package com.company.Project.service;

import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductTypeService {
    List<ProductType> getProductTypeList();
    ProductType getById(Integer id);
    ProductType add(ProductType productType);
    ProductType update(Integer id,ProductType productType);
    void delete(Integer id);
}
