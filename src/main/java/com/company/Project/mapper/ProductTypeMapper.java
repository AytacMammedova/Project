package com.company.Project.mapper;

import com.company.Project.model.dto.ProductTypeDto;
import com.company.Project.model.entity.ProductType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel ="spring")
public interface ProductTypeMapper {
    ProductTypeDto toProductTypeDto(ProductType productType);
    List<ProductTypeDto>toProductTypeDtoList(List<ProductType> productTypeList);

}
