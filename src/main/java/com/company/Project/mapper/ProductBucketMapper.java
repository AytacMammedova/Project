package com.company.Project.mapper;

import com.company.Project.model.dto.ProductBucketDto;
import com.company.Project.model.entity.ProductBucket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel ="spring")
public interface ProductBucketMapper {
    List<ProductBucketDto>toProductBucketDtoList(List<ProductBucket>productBucketList);

}
