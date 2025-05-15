package com.company.Project.mapper;

import com.company.Project.model.dto.request.CategoryAddDto;
import com.company.Project.model.dto.CategoryDto;
import com.company.Project.model.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel ="spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
    List<CategoryDto> toCategoryDtoList(List<Category> categoryList);
    Category toCategory(CategoryAddDto categoryAddDto);
}
