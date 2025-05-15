package com.company.Project.mapper;

import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.entity.Bucket;
import org.mapstruct.Mapper;

@Mapper(componentModel ="spring")
public interface BucketMapper {
    BucketDto toBucketDto(Bucket bucket);
}
