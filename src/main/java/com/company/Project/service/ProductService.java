package com.company.Project.service;

import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductDto> getProductsList();
    Page<ProductDto> searchProducts(ProductSearchCriteria criteria, Pageable pageable);
    ProductDto getById(Long id);
    List<ProductDto>getProductsBySubtypeId(Integer subtypeId);
    ProductDto add(ProductAddDto productAddDto);
    ProductDto update(Long id,ProductAddDto productAddDto);
    void delete(Long id);
}
