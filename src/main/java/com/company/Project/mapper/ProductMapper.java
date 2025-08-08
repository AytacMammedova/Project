package com.company.Project.mapper;

import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
@Mapper(componentModel ="spring")
public interface ProductMapper {
    ProductDto toProductDto(Product product);
    Product toProduct(ProductAddDto productAddDto);
    List<ProductDto> toProductDtoList(List<Product>products);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateProduct(ProductAddDto productAddRequestDto, @MappingTarget Product product);
}
