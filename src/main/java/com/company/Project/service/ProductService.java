package com.company.Project.service;

import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductDto> getProductsList();
    ProductDto getById(Integer id);
    List<ProductDto>getProductsBySubtypeId(Integer subtypeId);
    ProductDto add(ProductAddDto productAddDto);
    ProductDto update(Integer id,ProductAddDto productAddDto);
    void delete(Integer id);
}
