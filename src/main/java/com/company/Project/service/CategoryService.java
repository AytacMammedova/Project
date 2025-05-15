package com.company.Project.service;

import com.company.Project.model.dto.request.CategoryAddDto;
import com.company.Project.model.dto.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> getCategoryList();
    CategoryDto getById(Integer id);
    CategoryDto add(CategoryAddDto categoryAddDto);
    CategoryDto update(Integer id,CategoryAddDto categoryAddDto);
    void delete(Integer id);
}
