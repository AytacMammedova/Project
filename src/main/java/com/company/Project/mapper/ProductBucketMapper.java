package com.company.Project.mapper;

import com.company.Project.model.dto.ProductBucketDto;
import com.company.Project.model.entity.ProductBucket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ProductBucketMapper {
    @Mapping(source = "id", target = "bucketProductId")
    ProductBucketDto toProductBucketDto(ProductBucket productBucket);
}