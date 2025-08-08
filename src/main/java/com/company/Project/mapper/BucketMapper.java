package com.company.Project.mapper;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.entity.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel ="spring")
public interface BucketMapper {
    @Mapping(source = "payment.id", target = "paymentId")
    BucketDto toBucketDto(Bucket bucket);
}
