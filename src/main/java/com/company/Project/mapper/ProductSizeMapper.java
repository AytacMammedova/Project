package com.company.Project.mapper;

import com.company.Project.model.dto.ProductSizeDto;
import com.company.Project.model.entity.ProductSize;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductSizeMapper {
    ProductSizeDto toProductSizeDto(ProductSize productSize);
    List<ProductSizeDto> toProductSizeDtoList(List<ProductSize> productSizes);
}