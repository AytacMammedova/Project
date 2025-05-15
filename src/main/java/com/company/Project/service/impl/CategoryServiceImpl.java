package com.company.Project.service.impl;

import com.company.Project.exceptions.CategoryAlreadyExistsException;
import com.company.Project.exceptions.CategoryNotFoundException;
import com.company.Project.mapper.CategoryMapper;
import com.company.Project.model.dto.request.CategoryAddDto;
import com.company.Project.model.dto.CategoryDto;
import com.company.Project.model.entity.Category;
import com.company.Project.repository.CategoryRepository;
import com.company.Project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategoryList() {
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll());
    }

    @Override
    public CategoryDto getById(Integer id) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("No category with id: "+id)));
    }

    @Override
    public CategoryDto add(CategoryAddDto categoryAddDto) {
        if(categoryRepository.findByName(categoryAddDto.getName()).isPresent()){
            throw new CategoryAlreadyExistsException("This category already exists");
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryAddDto)));
    }

    @Override
    public CategoryDto update(Integer id, CategoryAddDto categoryAddDto) {
        Category category=categoryRepository.findById(id).orElseThrow(IllegalStateException::new);
        if(Objects.nonNull(categoryAddDto.getName())){
            category.setName(categoryAddDto.getName());
        }
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }
}
