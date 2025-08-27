package com.company.Project.service.impl;

import com.company.Project.exceptions.CategoryNotFoundException;
import com.company.Project.exceptions.ProductNotFoundException;
import com.company.Project.mapper.ProductMapper;
import com.company.Project.model.Gender;
import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductSearchCriteria;
import com.company.Project.model.entity.SubType;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.SubTypeRepository;
import com.company.Project.service.ProductService;
import com.company.Project.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final SubTypeRepository subTypeRepository;

    @Override
    public List<ProductDto> getProductsList() {
        log.info("Getting all products");
        return productMapper.toProductDtoList(productRepository.findAll());
    }

    public Page<ProductDto> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        log.info("Searching products with simple criteria: {}", criteria);

        Specification<Product> spec = Specification.where(null);

        spec = spec.and(ProductSpecification.hasName(criteria.getName()));
        spec = spec.and(ProductSpecification.hasPriceBetween(criteria.getMinPrice(), criteria.getMaxPrice()));
        spec = spec.and(ProductSpecification.hasGender(criteria.getGender()));
        spec = spec.and(ProductSpecification.hasColor(criteria.getColor()));
        spec = spec.and(ProductSpecification.hasSubType(criteria.getSubtypeId()));

        if (criteria.getInStock() != null && criteria.getInStock()) {
            spec = spec.and(ProductSpecification.isInStock());
        }

        if (criteria.getSortBy() != null) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(direction, criteria.getSortBy())
            );
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::toProductDto);
    }

    @Override
    public ProductDto getById(Integer id) {
        log.info("Getting product by ID: {}", id);
        return productMapper.toProductDto(productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found")));
    }

    @Override
    public List<ProductDto> getProductsBySubtypeId(Integer subtypeId) {
        return productMapper.toProductDtoList(productRepository.getBySubTypeId(subtypeId));
    }

    @Override
    public ProductDto add(ProductAddDto productAddDto) {
        log.info("Adding new product: {}", productAddDto.getName());
        Product product = productMapper.toProduct(productAddDto);
        product.setCreatedDate(LocalDate.now());

        if (productAddDto.getSubTypeId() != null) {
            SubType subType = subTypeRepository.findById(productAddDto.getSubTypeId())
                    .orElseThrow(() -> new CategoryNotFoundException("No SubType with ID: " + productAddDto.getSubTypeId()));
            product.setSubType(subType);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully with ID: {}", savedProduct.getId());
        return productMapper.toProductDto(savedProduct);
    }

    @Override
    public ProductDto update(Integer id,ProductAddDto productAddDto) {
        log.info("Updating product: {}", productAddDto.getName());
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        Product newProduct = productMapper.updateProduct(productAddDto, product);
        Product savedProduct = productRepository.save(newProduct);
        log.info("Product updated successfully");
        return productMapper.toProductDto(savedProduct);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
        log.info("Product deleted successfully");

    }
}
