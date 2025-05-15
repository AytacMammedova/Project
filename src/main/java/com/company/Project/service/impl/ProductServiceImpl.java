package com.company.Project.service.impl;

import com.company.Project.mapper.ProductMapper;
import com.company.Project.model.Gender;
import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import com.company.Project.repository.ProductRepository;
import com.company.Project.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    public List<ProductDto> getProductsList() {
        log.info("List of products");
        return productMapper.toProductDtoList(productRepository.findAll());
    }

    @Override
    public ProductDto getById(Integer id) {
        return productMapper.toProductDto(productRepository.findById(id).orElseThrow(NullPointerException::new));
    }

    @Override
    public List<ProductDto> getProductsBySubtypeId(Integer subtypeId) {
        return productMapper.toProductDtoList(productRepository.getBySubTypeId(subtypeId));
    }

    @Override
    public ProductDto add(ProductAddDto productAddDto) {
        Product product=productMapper.toProduct(productAddDto);
        product.setCreatedDate(LocalDate.now());
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto update(Integer id,ProductAddDto productAddDto) {
        Product product=productRepository.findById(id).orElseThrow(NullPointerException::new);
        if(Objects.nonNull(productAddDto.getName())){
            product.setName(productAddDto.getName());
        }
        if(Objects.nonNull(productAddDto.getDescription())){
            product.setDescription(productAddDto.getDescription());
        }
        if(Objects.nonNull(productAddDto.getPrice())){
            product.setPrice(productAddDto.getPrice());
        }
        if(Objects.nonNull(productAddDto.getStock())){
            product.setStock(productAddDto.getStock());
        }
        if(Objects.nonNull(productAddDto.getColor())){
            product.setColor(productAddDto.getColor());
        }
        if(Objects.nonNull(productAddDto.getGender())){
            product.setGender(productAddDto.getGender());
        }
        if(Objects.nonNull(productAddDto.getImage())){
            product.setImage(product.getImage());
        }
        product.setUpdatedDate(LocalDate.now());
        return productMapper.toProductDto(product);
    }

    @Override
    public void delete(Integer id) {
        log.info("Product deleted");
        productRepository.deleteById(id);

    }
}
