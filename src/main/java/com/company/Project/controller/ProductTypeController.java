package com.company.Project.controller;


import com.company.Project.model.dto.ProductTypeDto;
import com.company.Project.model.entity.ProductType;
import com.company.Project.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productTypes")
@RequiredArgsConstructor
public class ProductTypeController {
    private final ProductTypeService productTypeService;
    @GetMapping
    public List<ProductTypeDto> getProductTypeList(){
        return productTypeService.getProductTypeList();
    }
    @GetMapping("/{id}")
    public ProductTypeDto getById(@PathVariable Integer id){
        return productTypeService.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductTypeDto add(@RequestBody ProductType productType){
        return productTypeService.add(productType);
    }
    @PutMapping("/{id}")
    public ProductTypeDto update(@PathVariable Integer id,@RequestBody ProductType product){
        return productTypeService.update(id, product);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        productTypeService.delete(id);
    }
}



