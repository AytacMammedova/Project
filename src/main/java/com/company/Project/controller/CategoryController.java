package com.company.Project.controller;

import com.company.Project.model.dto.request.CategoryAddDto;
import com.company.Project.model.dto.CategoryDto;
import com.company.Project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public List<CategoryDto> getCategoryList(){
        return categoryService.getCategoryList();
    }
    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable Integer id){
        return categoryService.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@RequestBody CategoryAddDto categoryAddDto){
        return categoryService.add(categoryAddDto);
    }
    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Integer id,@RequestBody CategoryAddDto categoryAddDto){
        return categoryService.update(id, categoryAddDto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        categoryService.delete(id);
    }
}
