package com.company.Project.mapper;

import com.company.Project.model.dto.ProductBucketDto;
import com.company.Project.model.entity.ProductBucket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel ="spring")
public interface ProductBucketMapper {
//    @Mapping(source = "id.bucketProductId", target = "bucketProductId")
//    @Mapping(target = "product",ignore = true)
    ProductBucketDto toProductBucketDto(ProductBucket productBucket);
    List<ProductBucketDto>toProductBucketDtoList(List<ProductBucket>productBucketList);

}
