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
        log.info("Getting all categories");
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll());
    }

    @Override
    public CategoryDto getById(Integer id) {
        log.info("Getting category by id: {}", id);
        return categoryMapper.toCategoryDto(categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("No category with id: "+id)));
    }

    @Override
    public CategoryDto add(CategoryAddDto categoryAddDto) {
        log.info("Adding new category: {}", categoryAddDto.getName());
        if(categoryRepository.findByName(categoryAddDto.getName()).isPresent()){
            throw new CategoryAlreadyExistsException("This category already exists");
        }
        Category category=categoryMapper.toCategory(categoryAddDto);
        Category savedCategory=categoryRepository.save(category);
        log.info("Category added successfully with id: {}", savedCategory.getId());
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto update(Integer id, CategoryAddDto categoryAddDto) {
        log.info("Updating category with id: {}", id);

        Category category=categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("No category with id: "+id));
        if(Objects.nonNull(categoryAddDto.getName()) && !categoryAddDto.getName().trim().isEmpty()){
            categoryRepository.findByName(categoryAddDto.getName()).ifPresent(existingCategory -> {
                        if (!existingCategory.getId().equals(id)) {
                            throw new CategoryAlreadyExistsException("Category with name '" + categoryAddDto.getName() + "' already exists");
                        }
            });
            category.setName(categoryAddDto.getName().trim());
        }
        Category savedCategory = categoryRepository.save(category);
        log.info("Category updated successfully");
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("No category with id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully");
    }
}
